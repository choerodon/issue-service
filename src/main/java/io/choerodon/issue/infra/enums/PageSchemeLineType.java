package io.choerodon.issue.infra.enums;

/**
 * @author shinan.chen
 * @date 2018/8/23
 */
public enum PageSchemeLineType {

    DEFAULT("default"),
    CREATE("create"),
    EDIT("edit");

    private String typeName;

    PageSchemeLineType(String typeName) {
        this.typeName = typeName;
    }

    public String value(){
        return this.typeName;
    }

    public static Boolean contain(String typeName){
        for(PageSchemeLineType pageSchemeLineType:PageSchemeLineType.values()){
            if(pageSchemeLineType.value().equals(typeName)){
                return true;
            }
        }
        return false;
    }
}
