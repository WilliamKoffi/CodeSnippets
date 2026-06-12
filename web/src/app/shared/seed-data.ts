import { Snippet, Tag, User } from './types';

export const INITIAL_USER: User = {
  id: 'current_user',
  name: 'Ada Lovelace',
  handle: 'ada_lovelace',
  avatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuD5hez6rjSYs2_GJsu8299oQmxtBSJW1Kxq3lAlsRYid0L9TIVhQGWtBGVKHVRMxruuXSIDIzBAbirx-50YANaG6YXo8vnBscZJnzP7v8KizHHtpzd6eQ8hovFbROsWXoSmOslKy0VMAWyquekgryPKIXAW1NI4VeYxZ3s_R7nAUkuPPlyfJ428PXJFTXLCpCL50apfuuMvUUObVVWgtzJofBiYOgErzsZQRdEhe7I7436pY9lK6VCa3Rm_KqeUH7tczAtWQAzWSdFJ',
  reputation: 1540,
  role: 'fullstack',
  level: 'Elite Rank'
};

export const INITIAL_TAGS: Tag[] = [
  { name: 'react', count: '12k' },
  { name: 'python', count: '8.4k' },
  { name: 'rust', count: '5.1k' },
  { name: 'typescript', count: '3.9k' },
  { name: 'hooks', count: '2.5k' },
  { name: 'memory-leak', count: '1.2k' }
];


export const INITIAL_SNIPPETS: Snippet[] = [
  {
    id: 'bug_websocket_leak',
    title: 'Memory Leak (Fuite de mémoire) dans le nettoyage de useEffect de React',
    description: 'Je rencontre un avertissement persistant de fuite de mémoire lors du démontage d\'un composant qui configure une connexion WebSocket dans un hook useEffect. L\'avertissement indique spécifiquement que je tente de mettre à jour l\'état d\'un composant démonté. J\'ai inclus une fonction de nettoyage pour fermer la connexion, mais il semble que le gestionnaire de messages se déclenche encore ou que la fermeture capture une ancienne référence.',
    code: `import React, { useEffect, useState } from 'react';

export function LiveTicker() {
  const [price, setPrice] = useState(0);

  useEffect(() => {
    const ws = new WebSocket('wss://api.example.com/ticker');

    ws.onmessage = (event) => {
      const data = JSON.parse(event.data);
      // L'avertissement se produit ici lors du démontage
      setPrice(data.price); 
    };

    return () => {
      // Le nettoyage semble insuffisant
      ws.close();
    };
  }, []);

  return <div>Prix Actuel: {price}</div>;
}`,
    language: 'tsx',
    tags: ['react', 'hooks', 'memory-leak'],
    author: {
      name: 'dev_master',
      handle: 'dev_master',
      avatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuA8b3UV0kYP1eOV7qF9EJHCn5TObmh8FOcosVLpvUMDOQctwVKvOOFarJdqHix_SPoc_23MVF-EArQO1DMV6B7c8IEw9NpaQTr1x81_Eg5kNXLF331caiuSIAvhSeDHdJTCRx03Qs4JIsvHxpkZpxNpCH6chXwadTdkXIOsVQTDvyJmDaiDUza7OGraCvinJItlnUZUzYoRtMVscdxyLYznOSHUYr1mj9eezNN_6X-OX9nWY3_srVsS3h7MUJ9GifrkNW6NaoLu61a-',
      reputation: '14.2k',
      isAuthor: true
    },
    likes: 128,
    solutionsCount: 2,
    createdAt: 'Il y a 2 heures',
    type: 'bug',
    solutions: [
      {
        id: 'solution_1',
        author: {
          name: 'react_ninja',
          avatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuD5hez6rjSYs2_GJsu8299oQmxtBSJW1Kxq3lAlsRYid0L9TIVhQGWtBGVKHVRMxruuXSIDIzBAbirx-50YANaG6YXo8vnBscZJnzP7v8KizHHtpzd6eQ8hovFbROsWXoSmOslKy0VMAWyquekgryPKIXAW1NI4VeYxZ3s_R7nAUkuPPlyfJ428PXJFTXLCpCL50apfuuMvUUObVVWgtzJofBiYOgErzsZQRdEhe7I7436pY9lK6VCa3Rm_KqeUH7tczAtWQAzWSdFJ',
          reputation: '4.5k'
        },
        votes: 42,
        content: 'Le problème est que `ws.close()` est asynchrone, et l\'événement `onmessage` peut encore se déclencher juste après l\'initiation de la fermeture, mais avant l\'arrêt effectif. Le modèle React standard ici consiste à introduire un indicateur `isMounted` (ou un `AbortController` si vous effectuiez un fetch standard).',
        code: `  useEffect(() => {
    let isMounted = true; // Ajout du flag
    const ws = new WebSocket('wss://api.example.com/ticker');

    ws.onmessage = (event) => {
      if (!isMounted) return; // Clause de garde
      const data = JSON.parse(event.data);
      setPrice(data.price); 
    };

    return () => {
      isMounted = false; // Défini à false lors du démontage
      ws.close();
    };
  }, []);`,
        accepted: true,
        createdAt: 'Il y a 1 heure'
      },
      {
        id: 'solution_2',
        author: {
          name: 'clean_coder',
          avatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCfPZf94ExIFghhzSN4u-1Y_sNdApYvcPAA8NZe4YpoYnlBXw8UiF3LcAkItCS_oua0y6taHQzNkV8Tzl0m0cmN0tFsnbQPFPhrY2tuv0oT_2b1xCclGmEU4alprkuf8YQKGZCviu4HtX3nXtohT685YF_ZN2CdWj6FSInc5z0q4KS1w7-EG132nevkAb3mVitMctcx-nAKAajfU-wDIvFbvfsM84hpekRfhLXkzKREAUNmLBo7n0xYHYWSxxnaZ4xBXvaimYJ83qlQ',
          reputation: '820'
        },
        votes: 11,
        content: 'Vous pouvez aussi utiliser un `AbortController` customisé ou déporter l\'écoute du webSocket dans un gestionnaire de contexte global ou un store (de type Redux/Zustand) pour découpler la persistance des données du cycle de vie du composant unitaire.',
        accepted: false,
        createdAt: 'Il y a 45 minutes'
      }
    ]
  },
  {
    id: 'bug_useEffect_loop',
    title: 'Boucle infinie useEffect de React lors de la mise à jour de l\'état',
    description: 'J\'essaie de récupérer des données lorsqu\'un composant est monté et de les stocker dans le state local, mais le composant effectue un re-render à l\'infini. Comment résoudre ce comportement ? Qu\'ai-je manqué dans le tableau de dépendances de mon hook useEffect ?',
    code: `const [data, setData] = useState([]);

useEffect(() => {
  const fetchData = async () => {
    const result = await axios('/api/data');
    setData(result.data);
  };
  fetchData();
}); // Il manque le tableau de dépendances ici !`,
    language: 'react',
    tags: ['react', 'hooks', 'bug'],
    author: {
      name: 'dev_marco',
      handle: 'dev_marco',
      avatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuDGly0CNahXjZG_XK0BQggpq3SmpPgwyirWOyNZKsVCU8aS3Pbm2y53PvSUncSpLoJEoFcOP2vJcuIl5uPs4tfKnJpgi4WIe4-ts0J5_nkW4ZE9I4ZHi0Wep_A7cMNk5SxLgJUWE2XuUqpW002yM_YUieTUqzIGWvJkSO9_5_M-jf7LTnO9tjADgJxOt2d1yL424aC0Q_tJ6XUP1PjsTGtsfHVUy22SEZdqNRZeRE_6O4XBFmSdjS-Zc7NPZCwVOx-nLPnV39oagUfL',
      reputation: '3.1k'
    },
    likes: 124,
    solutionsCount: 5,
    createdAt: 'Il y a 2 heures',
    type: 'bug',
    solutions: []
  },
  {
    id: 'bug_typescript_map',
    title: 'TypeScript ne trouve pas le nom \'Map\' en mode strict',
    description: 'Je configure un nouveau projet en TypeScript strict avec la cible ES5 et j\'obtiens l\'erreur d\'analyse du compilateur : "Cannot find name \'Map\'". Comment dois-je mettre à jour la configuration de mon compilateur dans mon fichier tsconfig.json pour inclure les types d\'objets globaux ES6 comme Map, Set et Promise ?',
    code: `// Erreur du compilateur TypeScript :
const userRegistry = new Map<string, User>();
// ❌ Cannot find name 'Map'.`,
    language: 'typescript',
    tags: ['typescript', 'compiler-error'],
    author: {
      name: 'sarah_codes',
      handle: 'sarah_codes',
      avatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuAC4e2IB41tfth66MxZ2xFv6YhjauxtWFVta5Pzi4IABhSleKxBUkLaGkz79K4u292uS5EIH-EM5Za4HGndw28G3f3cVETGXgtguz9_6DVqIoARBruVD27LO8zMSaBC30tifPdMCeOniSj3CazaJz0HmZto05hf61zbyx-jWMbpET4-nBP48DUG1b1obHTS2uiebVEpo5bOioB19j8Z1KtE2olMk2MDSwTkxsyMG2Ue0yoOooeLYDXNmMUwcU7NY8AqCEl_hGaIsLiD',
      reputation: '9.8k'
    },
    likes: 89,
    solutionsCount: 12,
    createdAt: 'Il y a 4 heures',
    type: 'bug',
    solutions: []
  }
];
