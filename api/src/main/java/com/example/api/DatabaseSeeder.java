package com.example.api;

import com.example.api.domains.auth.User;
import com.example.api.domains.auth.repositories.UserRepository;
import com.example.api.domains.snippets.repositories.SnippetRepository;
import com.example.api.domains.snippets.repositories.TagRepository;
import com.example.api.domains.snippets.domain.Snippet;
import com.example.api.domains.snippets.domain.Solution;
import com.example.api.domains.snippets.domain.Tag;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final SnippetRepository snippetRepository;

    public DatabaseSeeder(
            UserRepository userRepository,
            TagRepository tagRepository,
            SnippetRepository snippetRepository) {
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.snippetRepository = snippetRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (snippetRepository.count() > 0 && userRepository.existsById("current_user")) {
            System.out.println("Database already seeded with snippets and default user. Skipping seeder.");
            return;
        }

        System.out.println("Clearing old tables...");
        snippetRepository.deleteAll();
        tagRepository.deleteAll();
        userRepository.deleteAll();

        System.out.println("Seeding database with default users, tags, snippets, and solutions...");

        String defaultHashedPassword = hashPassword("password");

        // 1. Seed Users
        User ada = new User(
                "current_user",
                "Ada Lovelace",
                "ada_lovelace",
                "ada@lovelace.com",
                defaultHashedPassword,
                "https://lh3.googleusercontent.com/aida-public/AB6AXuD5hez6rjSYs2_GJsu8299oQmxtBSJW1Kxq3lAlsRYid0L9TIVhQGWtBGVKHVRMxruuXSIDIzBAbirx-50YANaG6YXo8vnBscZJnzP7v8KizHHtpzd6eQ8hovFbROsWXoSmOslKy0VMAWyquekgryPKIXAW1NI4VeYxZ3s_R7nAUkuPPlyfJ428PXJFTXLCpCL50apfuuMvUUObVVWgtzJofBiYOgErzsZQRdEhe7I7436pY9lK6VCa3Rm_KqeUH7tczAtWQAzWSdFJ",
                1540,
                "fullstack",
                "Elite Rank"
        );

        User devMaster = new User(
                "dev_master",
                "dev_master",
                "dev_master",
                "dev_master@example.com",
                defaultHashedPassword,
                "https://lh3.googleusercontent.com/aida-public/AB6AXuA8b3UV0kYP1eOV7qF9EJHCn5TObmh8FOcosVLpvUMDOQctwVKvOOFarJdqHix_SPoc_23MVF-EArQO1DMV6B7c8IEw9NpaQTr1x81_Eg5kNXLF331caiuSIAvhSeDHdJTCRx03Qs4JIsvHxpkZpxNpCH6chXwadTdkXIOsVQTDvyJmDaiDUza7OGraCvinJItlnUZUzYoRtMVscdxyLYznOSHUYr1mj9eezNN_6X-OX9nWY3_srVsS3h7MUJ9GifrkNW6NaoLu61a-",
                14200,
                "fullstack",
                "Elite Rank"
        );

        User reactNinja = new User(
                "react_ninja",
                "react_ninja",
                "react_ninja",
                "react_ninja@example.com",
                defaultHashedPassword,
                "https://lh3.googleusercontent.com/aida-public/AB6AXuD5hez6rjSYs2_GJsu8299oQmxtBSJW1Kxq3lAlsRYid0L9TIVhQGWtBGVKHVRMxruuXSIDIzBAbirx-50YANaG6YXo8vnBscZJnzP7v8KizHHtpzd6eQ8hovFbROsWXoSmOslKy0VMAWyquekgryPKIXAW1NI4VeYxZ3s_R7nAUkuPPlyfJ428PXJFTXLCpCL50apfuuMvUUObVVWgtzJofBiYOgErzsZQRdEhe7I7436pY9lK6VCa3Rm_KqeUH7tczAtWQAzWSdFJ",
                4500,
                "frontend",
                "Pro"
        );

        User cleanCoder = new User(
                "clean_coder",
                "clean_coder",
                "clean_coder",
                "clean_coder@example.com",
                defaultHashedPassword,
                "https://lh3.googleusercontent.com/aida-public/AB6AXuCfPZf94ExIFghhzSN4u-1Y_sNdApYvcPAA8NZe4YpoYnlBXw8UiF3LcAkItCS_oua0y6taHQzNkV8Tzl0m0cmN0tFsnbQPFPhrY2tuv0oT_2b1xCclGmEU4alprkuf8YQKGZCviu4HtX3nXtohT685YF_ZN2CdWj6FSInc5z0q4KS1w7-EG132nevkAb3mVitMctcx-nAKAajfU-wDIvFbvfsM84hpekRfhLXkzKREAUNmLBo7n0xYHYWSxxnaZ4xBXvaimYJ83qlQ",
                820,
                "backend",
                "Aspirant"
        );

        User devMarco = new User(
                "dev_marco",
                "dev_marco",
                "dev_marco",
                "dev_marco@example.com",
                defaultHashedPassword,
                "https://lh3.googleusercontent.com/aida-public/AB6AXuDGly0CNahXjZG_XK0BQggpq3SmpPgwyirWOyNZKsVCU8aS3Pbm2y53PvSUncSpLoJEoFcOP2vJcuIl5uPs4tfKnJpgi4WIe4-ts0J5_nkW4ZE9I4ZHi0Wep_A7cMNk5SxLgJUWE2XuUqpW002yM_YUieTUqzIGWvJkSO9_5_M-jf7LTnO9tjADgJxOt2d1yL424aC0Q_tJ6XUP1PjsTGtsfHVUy22SEZdqNRZeRE_6O4XBFmSdjS-Zc7NPZCwVOx-nLPnV39oagUfL",
                3100,
                "fullstack",
                "Pro"
        );

        User sarahCodes = new User(
                "sarah_codes",
                "sarah_codes",
                "sarah_codes",
                "sarah_codes@example.com",
                defaultHashedPassword,
                "https://lh3.googleusercontent.com/aida-public/AB6AXuAC4e2IB41tfth66MxZ2xFv6YhjauxtWFVta5Pzi4IABhSleKxBUkLaGkz79K4u292uS5EIH-EM5Za4HGndw28G3f3cVETGXgtguz9_6DVqIoARBruVD27LO8zMSaBC30tifPdMCeOniSj3CazaJz0HmZto05hf61zbyx-jWMbpET4-nBP48DUG1b1obHTS2uiebVEpo5bOioB19j8Z1KtE2olMk2MDSwTkxsyMG2Ue0yoOooeLYDXNmMUwcU7NY8AqCEl_hGaIsLiD",
                9800,
                "devops",
                "Master"
        );

        saveUserIfMissing(ada);
        saveUserIfMissing(devMaster);
        saveUserIfMissing(reactNinja);
        saveUserIfMissing(cleanCoder);
        saveUserIfMissing(devMarco);
        saveUserIfMissing(sarahCodes);

        // 2. Seed Tags
        Tag react = tagRepository.save(new Tag("react"));
        Tag python = tagRepository.save(new Tag("python"));
        Tag rust = tagRepository.save(new Tag("rust"));
        Tag typescript = tagRepository.save(new Tag("typescript"));
        Tag hooks = tagRepository.save(new Tag("hooks"));
        Tag memoryLeak = tagRepository.save(new Tag("memory-leak"));
        Tag bug = tagRepository.save(new Tag("bug"));
        Tag compilerError = tagRepository.save(new Tag("compiler-error"));

        // 3. Seed Snippets
        Snippet snippet1 = new Snippet(
                "bug_websocket_leak",
                "Memory Leak (Fuite de mémoire) dans le nettoyage de useEffect de React",
                "Je rencontre un avertissement persistant de fuite de mémoire lors du démontage d'un composant qui configure une connexion WebSocket dans un hook useEffect. L'avertissement indique spécifiquement que je tente de mettre à jour l'état d'un composant démonté. J'ai inclus une fonction de nettoyage pour fermer la connexion, mais il semble que le gestionnaire de messages se déclenche encore ou que la fermeture capture une ancienne référence.",
                "import React, { useEffect, useState } from 'react';\n\nexport function LiveTicker() {\n  const [price, setPrice] = useState(0);\n\n  useEffect(() => {\n    const ws = new WebSocket('wss://api.example.com/ticker');\n\n    ws.onmessage = (event) => {\n      const data = JSON.parse(event.data);\n      // L'avertissement se produit ici lors du démontage\n      setPrice(data.price); \n    };\n\n    return () => {\n      // Le nettoyage semble insuffisant\n      ws.close();\n    };\n  }, []);\n\n  return <div>Prix Actuel: {price}</div>;\n}",
                "tsx",
                "bug",
                devMaster
        );
        snippet1.like();
        for (int i = 0; i < 127; i++) snippet1.like();
        snippet1.tag(Set.of(react, hooks, memoryLeak));

        Solution solution1 = new Solution(
                null,
                reactNinja,
                "Le problème est que `ws.close()` est asynchrone, et l'événement `onmessage` peut encore se déclencher juste après l'initiation de la fermeture, mais avant l'arrêt effectif. Le modèle React standard ici consiste à introduire un indicateur `isMounted` (ou un `AbortController` si vous effectuiez un fetch standard).",
                "  useEffect(() => {\n    let isMounted = true; // Ajout du flag\n    const ws = new WebSocket('wss://api.example.com/ticker');\n\n    ws.onmessage = (event) => {\n      if (!isMounted) return; // Clause de garde\n      const data = JSON.parse(event.data);\n      setPrice(data.price); \n    };\n\n    return () => {\n      isMounted = false; // Défini à false lors du démontage\n      ws.close();\n    };\n  }, []);"
        );
        for (int i = 0; i < 42; i++) solution1.upvote();
        solution1.accept();

        Solution solution2 = new Solution(
                null,
                cleanCoder,
                "Vous pouvez aussi utiliser un `AbortController` customisé ou déporter l'écoute du webSocket dans un gestionnaire de contexte global ou un store (de type Redux/Zustand) pour découpler la persistance des données du cycle de vie du composant unitaire.",
                null
        );
        for (int i = 0; i < 11; i++) solution2.upvote();

        snippet1.solve(solution1);
        snippet1.solve(solution2);
        snippetRepository.save(snippet1);

        Snippet snippet2 = new Snippet(
                "bug_useEffect_loop",
                "Boucle infinie useEffect de React lors de la mise à jour de l'état",
                "J'essaie de récupérer des données lorsqu'un composant est monté et de les stocker dans le state local, mais le composant effectue un re-render à l'infini. Comment résoudre ce comportement ? Qu'ai-je manqué dans le tableau de dépendances de mon hook useEffect ?",
                "const [data, setData] = useState([]);\n\nuseEffect(() => {\n  const fetchData = async () => {\n    const result = await axios('/api/data');\n    setData(result.data);\n  };\n  fetchData();\n}); // Il manque le tableau de dépendances ici !",
                "react",
                "bug",
                devMarco
        );
        for (int i = 0; i < 124; i++) snippet2.like();
        snippet2.tag(Set.of(react, hooks, bug));
        snippetRepository.save(snippet2);

        Snippet snippet3 = new Snippet(
                "bug_typescript_map",
                "TypeScript ne trouve pas le nom 'Map' en mode strict",
                "Je configure un nouveau projet en TypeScript strict avec la cible ES5 et j'obtiens l'erreur d'analyse du compilateur : \"Cannot find name 'Map'\". Comment dois-je mettre à jour la configuration de mon compilateur dans mon fichier tsconfig.json pour inclure les types d'objets globaux ES6 comme Map, Set et Promise ?",
                "// Erreur du compilateur TypeScript :\nconst userRegistry = new Map<string, User>();\n// ❌ Cannot find name 'Map'.",
                "typescript",
                "bug",
                sarahCodes
        );
        for (int i = 0; i < 89; i++) snippet3.like();
        snippet3.tag(Set.of(typescript, compilerError));
        snippetRepository.save(snippet3);

        System.out.println("Database seeding completed successfully!");
    }

    private void saveUserIfMissing(User user) {
        if (!userRepository.existsById(user.id())
            && !userRepository.existsByEmail(user.email())
            && !userRepository.existsByHandle(user.handle())) {
            userRepository.save(user);
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Failed to hash password", e);
        }
    }
}
