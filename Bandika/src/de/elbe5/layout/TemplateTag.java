package de.elbe5.layout;

import de.elbe5.request.RequestData;

import java.util.*;

public abstract class TemplateTag implements ITemplateNode {

    public static final String TAG_PREFIX = "tpl";

    protected String type = "";
    protected Map<String,String> parameters = new HashMap<>();

    protected List<ITemplateNode> childNodes = new ArrayList<>();

    protected TemplateTag(){
    }

    public String getType() {
        return type;
    }

    public void addChildNode(ITemplateNode node){
        childNodes.add(node);
    }

    public void setParameters(Map<String,String> parameters) {
        this.parameters = parameters;
    }

    public void appendTagStart(StringBuilder sb, RequestData rdata){
    }

    public void appendTagEnd(StringBuilder sb, RequestData rdata){
    }

    public void appendHtml(StringBuilder sb, RequestData rdata){
        appendTagStart(sb, rdata);
        if (!childNodes.isEmpty()) {
            appendInner(sb, rdata);
        }
        appendTagEnd(sb, rdata);
    }

    public String getStringParam(String key, RequestData rdata, String def){
        String result = parameters.get(key);
        if (result==null){
            return def;
        }
        if (result.contains("{{")){
            result = replaceParams(result, rdata.getTemplateAttributes());
        }
        return result;
    }

    public int getIntParam(String key, RequestData rdata, int def){
        try{
            String result = parameters.get(key);
            if (result==null){
                return def;
            }
            if (result.contains("{{")){
                result = replaceParams(result, rdata.getTemplateAttributes());
            }
            return Integer.parseInt(result);
        }
        catch (Exception e){
            return def;
        }
    }
    public boolean getBooleanParam(String key, RequestData rdata, boolean def){
        try{
            String result = parameters.get(key);
            if (result==null){
                return def;
            }
            if (result.contains("{{")){
                result = replaceParams(result, rdata.getTemplateAttributes());
            }
            return Boolean.parseBoolean(result);
        }
        catch (Exception e){
            return def;
        }
    }

    public void appendInner(StringBuilder sb, RequestData rdata){
        for (ITemplateNode node : childNodes) {
            node.appendHtml(sb, rdata);
        }
    }

    @Override
    public void appendCode(StringBuilder sb){
        sb.append("<").append(TAG_PREFIX).append(":").append(type);
        for (String key : parameters.keySet()){
            sb.append(" ").append(key).append("=\"").append(parameters.get(key)).append("\"");
        }
        if (childNodes.isEmpty()){
            sb. append(" />");
        }
        else {
            sb.append(">");
            appendChildCode(sb);
            sb.append("</").append(TAG_PREFIX).append(":").append(type).append(">");
        }
    }

    public void appendChildCode(StringBuilder sb){
        for (ITemplateNode node : childNodes) {
            node.appendCode(sb);
        }
    }

}
