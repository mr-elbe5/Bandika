package de.elbe5.template;

import de.elbe5.application.ApplicationPath;
import de.elbe5.base.FileHelper;
import de.elbe5.request.RequestData;

import java.util.ArrayList;
import java.util.List;

public class Template implements ITemplateNode {

    public static String templateBasePath = ApplicationPath.getAppWEBINFPath()+"/_template/";

    public static boolean includePage(StringBuilder sb, String path, RequestData rdata){
        Template include = TemplateCache.getTemplate(path);
        if (include != null) {
            sb.append(include.getHtml(rdata));
            return true;
        }
        return false;
    }

    final String path;

    List<ITemplateNode> childNodes = new ArrayList<>();

    public Template(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getFullPath() {
        return templateBasePath + path + ".shtml";
    }

    public List<ITemplateNode> getChildNodes() {
        return childNodes;
    }

    public void addChildNode(ITemplateNode node){
        childNodes.add(node);
    }

    public String getHtml(RequestData rdata){
        StringBuilder sb = new StringBuilder();
        appendHtml(sb, rdata);
        return sb.toString();
    }

    public String getCode(){
        StringBuilder sb = new StringBuilder();
        appendCode(sb);
        return sb.toString();
    }

    public void appendHtml(StringBuilder sb, RequestData rdata) {
        for (ITemplateNode node : childNodes) {
            node.appendHtml(sb, rdata);
        }
    }

    @Override
    public void appendCode(StringBuilder sb) {
        for (ITemplateNode node : childNodes) {
            node.appendCode(sb);
        }
    }

    public boolean loadFromFile(){
        String code = FileHelper.readTextFile(getFullPath());
        if (code.isEmpty()) {
            return false;
        }
        return new TemplateParser(code, this).parse();
    }
}
