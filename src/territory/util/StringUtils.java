package territory.util;

public class StringUtils {
    /**
     * Capitalize the given string, for example
     * HELLO THERE -> Hello There
     *
     * Treat underscores as spaces, so
     * HELLO_THERE -> Hello There
     *
     * @param str the string to capitalize
     * @return str, but capitalized
     */
    public static String toCapitalCase(String str){
        //split on spaces and underscores
        String[] words = str.split("[_ ]");

        StringBuilder capitalCase = new StringBuilder(toCapitalCaseWord(words[0]));

        for(int i = 1; i < words.length; i++){
            capitalCase.append(" ").append(toCapitalCaseWord(words[i]));
        }

        return capitalCase.toString();
    }

    /**
     * Capitalize a single word
     * @param word the word to capitalize
     * @return the word, but capitalized
     */
    private static String toCapitalCaseWord(String word){
        //if its just one character, upper case it
        if(word.length() < 1){
            return word.toUpperCase();
        }

        char first = word.charAt(0);
        String rest = word.substring(1);

        return Character.toUpperCase(first) + rest.toLowerCase();
    }
}
