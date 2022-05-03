package de.elbe5.serverpage;

import de.elbe5.application.ApplicationPath;
import de.elbe5.base.FileHelper;
import de.elbe5.base.Log;
import de.elbe5.request.RequestData;

import java.util.ArrayList;
import java.util.List;

public class ServerPage implements SPNode {

    public static String shtmlBasePath = ApplicationPath.getAppWEBINFPath()+"/_shtml/";

    public static boolean includePage(StringBuilder sb, String path, RequestData rdata){
        ServerPage include = SPPageCache.getPage(path);
        if (include != null) {
            sb.append(include.getHtml(rdata));
            return true;
        }
        return false;
    }

    final String path;

    List<SPNode> childNodes = new ArrayList<>();

    public ServerPage(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getFullPath() {
        return shtmlBasePath + path + ".shtml";
    }

    public List<SPNode> getChildNodes() {
        return childNodes;
    }

    public void addChildNode(SPNode node){
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

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        for (SPNode node : childNodes) {
            node.appendHtml(sb, rdata);
        }
    }

    @Override
    public void appendCode(StringBuilder sb) {
        for (SPNode node : childNodes) {
            node.appendCode(sb);
        }
    }

    public boolean loadFromFile(){
        String code = FileHelper.readTextFile(getFullPath());
        if (code.isEmpty()) {
            return false;
        }
        return new SPParser(code, this).parse();
    }
}
