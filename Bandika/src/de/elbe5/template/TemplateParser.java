package de.elbe5.template;

import de.elbe5.base.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateParser {

    private final String code;

    private final char[] tagStartChars = ("<" + TemplateTag.TAG_PREFIX + ":").toCharArray();
    private final char[] tagEndChars = ("</" + TemplateTag.TAG_PREFIX + ":").toCharArray();
    private final char[] endChar = ">".toCharArray();

    private final Template page;
    private final List<TemplateTag> tagStack = new ArrayList<>();

    public TemplateParser(String code, Template page){
        this.code = code;
        this.page = page;
    }

    public boolean parse(){
        char[] chars = code.toCharArray();
        List<TagData> indices = new ArrayList<>();
        int p1 = 0;
        // get start tags
        while (true){
            int tagStart = nextOccurrenceOf(chars, tagStartChars, p1);
            if (tagStart == -1){
                break;
            }
            int tagEnd = nextOccurrenceOf(chars,endChar, tagStart);
            if (tagEnd == -1){
                error("no tag end in " + page.getName());
                return false;
            }
            boolean selfClosing = code.charAt(tagEnd-1) == '/';
            int contentStart = tagStart + tagStartChars.length;
            int contentEnd = selfClosing ? tagEnd-1 : tagEnd;
            indices.add(new TagData(tagStart, tagEnd, code.substring(contentStart, contentEnd).trim(), true, selfClosing));
            p1 = tagEnd;
        }
        // get end tags
        p1 = 0;
        while (true) {
            int tagStart = nextOccurrenceOf(chars, tagEndChars, p1);
            if (tagStart == -1){
                break;
            }
            int tagEnd = nextOccurrenceOf(chars,endChar, tagStart);
            if (tagEnd == -1){
                error("no tag end in " + page.getName());
                return false;
            }
            int contentStart = tagStart + tagEndChars.length;
            indices.add(new TagData(tagStart, tagEnd, code.substring(contentStart, tagEnd).trim(), false, false));
            p1 = tagEnd;
        }
        // sort by start index
        indices.sort(null);
        int start = 0;
        // create tag and text objects
        for (TagData ip : indices){
            //log("processing "+ (ip.isStartIndex ? "tag start " : "tag end ") + ip.name + " at " + ip.start);
            if (ip.start > start){
                addChildNode(new TemplateText(code.substring(start, ip.start)));
            }
            start = ip.end+1;
            if (ip.isStartIndex){
                pushTag(ip.name, ip.getParameters());
                if (ip.isSelfClosing){
                    if (!popTag(ip.name))
                        return false;
                }
            }
            else{
                if (!popTag(ip.name))
                    return false;
            }
        }
        if (start < code.length()-1){
            addChildNode(new TemplateText(code.substring(start)));
        }
        return true;
    }

    // add tag and put on stack top
    private void pushTag(String type, Map<String,String> params){
        //log("push tag " + type);
        TemplateTag tag;
        tag = TemplateTagFactory.createTag(type);
        if (tag == null){
            error("tag type not found: " + type + " in " + page.getName());
            return;
        }
        tag.setParameters(params);
        addChildNode(tag);
        tagStack.add(tag);
    }

    // remove from stack top
    private boolean popTag(String type){
        //log("pop tag " + type);
        if (tagStack.size()==0){
            error("bad closing tag: " + type + " in " + page.getName());
            return false;
        }
        if (!tagStack.get(tagStack.size()-1).getType().equals(type)){
            error("bad closing tag: " + type + " does not match " + tagStack.get(tagStack.size()-1).getType() + " in " + page.getName());
            return false;
        }
        tagStack.remove(tagStack.size()-1);
        return true;
    }

    private void addChildNode(ITemplateNode node){
        if (tagStack.size()>0) {
            tagStack.get(tagStack.size() - 1).addChildNode(node);
        }
        else{
            page.addChildNode(node);
        }
    }

    // like indexOf, but skipping string content
    protected int nextOccurrenceOf(char[] src, char[] target, int startPos){
        boolean inString = false;
        boolean match;
        for (int i=startPos; i<src.length; i++){
            char ch = src[i];
            if (ch == '\"') {
                inString = !inString;
            } else {
                if (!inString && ch == target[0] && i + target.length < src.length) {
                    match = true;
                    for (int j = 1; j < target.length && i+j<src.length; j++) {
                        if (target[j] != src[i + j]) {
                            match = false;
                            break;
                        }
                    }
                    if (match) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    static class TagData implements Comparable<TagData>{
            int start;
            int end;
            boolean isStartIndex;
            boolean isSelfClosing;
            String name;
            String content;

        public TagData(int start, int end, String content, boolean isStartIndex, boolean isSelfClosing){
            this.start = start;
            this.end = end;
            this.isStartIndex = isStartIndex;
            this.isSelfClosing = isSelfClosing;
            int idx = content.indexOf(" ");
            if (idx != -1) {
                name = content.substring(0, idx).trim();
                this.content = content.substring(idx+1).trim();
            }
            else{
                name = content.trim();
                this.content = "";
            }
            //log((isStartIndex ? "adding tag start " : "adding tag end ") + name + " at " + this.start);
        }

        public Map<String,String> getParameters(){
            Map<String,String> params = new HashMap<>();
            StringBuilder sb = new StringBuilder();
            String key = "";
            boolean inString = false;
            for (char ch : content.toCharArray()) {
                switch (ch) {
                    case '\"':
                        if (inString) {
                            if (!key.isEmpty()) {
                                params.put(key,sb.toString());
                                key = "";
                                sb = new StringBuilder();
                            }
                        }
                        inString = !inString;
                        break;
                    case '=':
                        key = sb.toString();
                        sb = new StringBuilder();
                        break;
                    case ' ':
                        if (!inString) {
                        break;
                    }
                    default:
                        sb.append(ch);
                }
            }
            return params;
        }

        @Override
        public int compareTo(TagData o) {
            return start - o.start;
        }
    }

    static void log(String log){
        //System.out.println(log);
        Log.log(log);
    }
    static void error(String err){
        //System.out.println(err);
        Log.error(err);
    }

}
