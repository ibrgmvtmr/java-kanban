import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class Main{
    public static void main(String[] args) {
        String s = "bb";
        System.out.println(Solution.lengthOfLongestSubstring(s));
    }
}
class Solution {
    public static int lengthOfLongestSubstring(String s) { //abcabcd
        Map<Character, Integer> map = new HashMap<>();
        int maxLength = 0;
        for (int right = 0, left = 0; right < s.length(); right++) {
            char ch = s.charAt(right); //5
            if(map.containsKey(ch) && map.get(ch) >= left){
                left = map.get(ch)+1;//1
            }
            maxLength = Math.max(maxLength, right-left+1);//0
            map.put(ch, right); //4
        }

        return maxLength;
    }
}