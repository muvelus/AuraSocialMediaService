package com.lit.fire.flame;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Sample {


        public static void main(String[] args) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String rawPassword = "password123";
            String hashedPassword = encoder.encode(rawPassword);
            System.out.println("BCrypt hash: " + hashedPassword);

            // Verify the hash
            boolean matches = encoder.matches(rawPassword, hashedPassword);
            System.out.println("Password matches: " + matches);
        }
}
