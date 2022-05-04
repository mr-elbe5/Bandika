package de.elbe5.serverpage;

import de.elbe5.base.StringMap;

import java.util.ArrayList;
import java.util.List;

public class SPParser {

    private final String code;

    private final char[] tagStartChars = ("<" + SPNode.TAG_PREFIX + ":").toCharArray();
    private final char[] tagEndChars = ("</" + SPNode.TAG_PREFIX + ":").toCharArray();
    private final char[] endChar = ">".toCharArray();

    private final ServerPage page;
    private final List<SPTag> tagStack = new ArrayList<>();

    public SPParser(String code, ServerPage page){
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
                System.out.println("no tag end");
                return false;
            }
            boolean selfClosing = code.charAt(tagEnd-1) == '/';
            int contentStart = tagStart + tagStartChars.length;
            int contentEnd = selfClosing ? tagEnd-1 : tagEnd;
            indices.add(new TagData(tagStart, tagEnd, code.substring(contentStart, contentEnd).trim(), true, selfClosing));
            p1 = tagEnd+1;
        }
        // get end tags
        while (true) {
            int tagStart = nextOccurrenceOf(chars, tagEndChars, p1);
            if (tagStart == -1){
                break;
            }
            int tagEnd = nextOccurrenceOf(chars,endChar, tagStart);
            if (tagEnd == -1){
                System.out.println("no tag end");
                return false;
            }
            int contentStart = tagStart + tagEndChars.length;
            indices.add(new TagData(tagStart, tagEnd, code.substring(contentStart, tagEnd).trim(), false, false));
            p1 = tagEnd+1;
        }
        // sort by start index
        indices.sort(null);
        int start = 0;
        // create tag and text objects
        for (TagData ip : indices){
            if (ip.start > start){
                addChildNode(new SPText(code.substring(start, ip.start)));
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
            addChildNode(new SPText(code.substring(start)));
        }
        return true;
    }

    // add tag and put on stack top
    private void pushTag(String type, StringMap params){
        SPTag tag;
        tag = SPTagFactory.createTag(type);
        tag.collectParameters(params);
        addChildNode(tag);
        tagStack.add(tag);
    }

    // remove from stack top
    private boolean popTag(String type){
        if (tagStack.size()==0){
            System.out.println("bad closing tag: " + type);
            return false;
        }
        if (!tagStack.get(tagStack.size()-1).getType().equals(type)){
            System.out.println("bad closing tag: " + type + " does not match " + tagStack.get(tagStack.size()-1).getType());
            return false;
        }
        tagStack.remove(tagStack.size()-1);
        return true;
    }

    private void addChildNode(SPNode node){
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

        }

        public StringMap getParameters(){
            StringMap map = new StringMap();
            StringBuilder sb = new StringBuilder();
            String key = "";
            boolean inString = false;
            for (char ch : content.toCharArray()) {
                switch (ch) {
                    case '\"':
                        if (inString) {
                            if (!key.isEmpty()) {
                                map.put(key,sb.toString());
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
            return map;
        }

        @Override
        public int compareTo(TagData o) {
            return start - o.start;
        }
    }

}
