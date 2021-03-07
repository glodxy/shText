package util;

public class Text {
    public static int getCharNum(String text){
        int words_count = 0;
        text=text.trim();

        String cn_words = text.replaceAll("[^(\\u4e00-\\u9fa5，。《》？；’‘：“”【】、）（……￥！·)]", "");
        int cn_words_count = cn_words.length();

        String non_cn_words = text.replaceAll("[^(a-zA-Z0-9`\\-=\';.,/~!@#$%^&*()_+|}{\":><?\\[\\])]", " ");
        int non_cn_words_count = 0;
        String[] ss = non_cn_words.split(" ");
        for(String s:ss){
            if(s.trim().length()!=0) non_cn_words_count++;
        }

        words_count = cn_words_count + non_cn_words_count;
        return words_count;
    }
}
