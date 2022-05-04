package de.elbe5.serverpage;

import de.elbe5.request.RequestData;

import java.util.*;

public class SPTag implements SPNode {

    protected String type = "";
    protected Map<String,String> parameters = new HashMap<>();

    protected List<SPNode> childNodes = new ArrayList<>();

    protected SPTag(){
    }

    public String getType() {
        return type;
    }

    public void addChildNode(SPNode node){
        childNodes.add(node);
    }

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata){
        appendTagStart(sb, rdata);
        if (!childNodes.isEmpty()) {
            appendInner(sb, rdata);
        }
        appendTagEnd(sb, rdata);
    }

    public void setParameters(Map<String,String> parameters) {
        this.parameters = parameters;
    }

    public String getStringParam(String key, RequestData rdata, String def){
        String result = parameters.get(key);
        if (result==null){
            return def;
        }
        if (result.contains("{{")){
            result = replaceParams(result, rdata.getPageAttributes());
        }
        return result;
    }

    public String getStringParam(String key, RequestData rdata){
        return getStringParam(key, rdata, "");
    }

    public int getIntParam(String key, RequestData rdata, int def){
        try{
            String result = parameters.get(key);
            if (result==null){
                return def;
            }
            if (result.contains("{{")){
                result = replaceParams(result, rdata.getPageAttributes());
            }
            return Integer.parseInt(result);
        }
        catch (Exception e){
            return def;
        }
    }
    public int getIntParam(String key, RequestData rdata){
        return getIntParam(key, rdata,0);
    }

    public boolean getBooleanParam(String key, RequestData rdata, boolean def){
        try{
            String result = parameters.get(key);
            if (result==null){
                return def;
            }
            if (result.contains("{{")){
                result = replaceParams(result, rdata.getPageAttributes());
            }
            return Boolean.parseBoolean(result);
        }
        catch (Exception e){
            return def;
        }
    }

    public boolean getBooleanParam(String key, RequestData rdata){
        return getBooleanParam(key, rdata, false);
    }

    public void appendTagStart(StringBuilder sb, RequestData rdata){
    }

    public void appendInner(StringBuilder sb, RequestData rdata){
        for (SPNode node : childNodes) {
            node.appendHtml(sb, rdata);
        }
    }

    public void appendTagEnd(StringBuilder sb, RequestData rdata){
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
        for (SPNode node : childNodes) {
            node.appendCode(sb);
        }
    }

    protected void includePage(StringBuilder sb, String path, RequestData rdata){
        ServerPage include = SPPageCache.getPage(path);
        if (include != null) {
            sb.append(include.getHtml(rdata));
        }
    }

}