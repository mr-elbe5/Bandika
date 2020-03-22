package de.elbe5.page;

import de.elbe5.base.log.Log;

import java.lang.reflect.Constructor;

class SectionPartClassInfo {

    private String type;
    private Constructor<? extends SectionPartData> ctor;
    private SectionPartBean bean;
    private boolean useLayouts=false;

    public SectionPartClassInfo(Class<? extends SectionPartData> contentClass, SectionPartBean bean, boolean useLayouts){
        type = contentClass.getSimpleName();
        try {
            ctor = contentClass.getConstructor();
        } catch (Exception e) {
            Log.error("no valid constructor found", e);
        }
        this.bean=bean;
        this.useLayouts=useLayouts;
    }

    public SectionPartData getNewData(){
        try {
            return ctor.newInstance();
        } catch (Exception e) {
            Log.error("could not create page part data for type "+type);
        }
        return null;
    }

    public SectionPartBean getBean(){
        return bean;
    }

    public boolean useLayouts() {
        return useLayouts;
    }
}
