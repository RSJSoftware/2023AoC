package utils;

import java.util.ArrayList;

public class AoCMath {
    public static long lcm(ArrayList<Long> nums) {
        long output = 1L;
        int divisor = 2;

        while (true) {
            int counter = 0;
            boolean divisible = false;

            for (int i = 0; i < nums.size(); i++) {
                if (nums.get(i) == 0)
                    return 0;
                if (nums.get(i) < 0)
                    nums.set(i, Math.abs(nums.get(i)));
                if (nums.get(i) == 1)
                    counter++;

                if (nums.get(i) % divisor == 0) {
                    divisible = true;
                    nums.set(i, nums.get(i) / divisor);
                }
            }

            if (divisible)
                output *= divisor;
            else
                divisor++;

            if (counter == nums.size())
                return output;
        }

    }

}
