package classes.util;

public class PermissionConvert {
    private static final String P_VIEW="可查看";
    private static final String P_EDIT="可编辑";
    private static final String P_ALL="所有权限";

    public static String getNameByPermission(int p){
        switch(p){
            case 1:return P_VIEW;
            case 2:return P_EDIT;
            case 3:return P_ALL;
            default:return null;
        }
    }

    public static int getPermissionByName(String n){
        switch(n){
            case P_VIEW:return 1;
            case P_EDIT:return 2;
            case P_ALL:return 3;
            default:return -1;
        }
    }
}
