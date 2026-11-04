/* Service Worker — Giochi Bimbi v0.8
 * Strategia: cache-first. Le pagine sono file HTML autonomi
 * (font incluso in base64), quindi basta pre-cachare tutto una volta
 * e funziona per sempre offline. Cambiare CACHE_NAME a ogni release
 * per forzare l'aggiornamento.
 */
const CACHE_NAME = 'giochi-bimbi-v0.8.4';

const ASSETS = [
  './',
  './index.html',
  './bolidi.html',
  './manifest.json',
  './icons/icon-192.png',
  './icons/icon-512.png',
  './icons/icon-512-maskable.png',
  './icons/apple-touch-icon.png'
];

self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => cache.addAll(ASSETS))
  );
  self.skipWaiting();
});

self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches.keys().then((keys) =>
      Promise.all(
        keys
          .filter((k) => k !== CACHE_NAME)
          .map((k) => caches.delete(k))
      )
    ).then(() => self.clients.claim())
  );
});

self.addEventListener('fetch', (event) => {
  // Cache-first, con fallback alla rete solo se manca in cache
  event.respondWith(
    caches.match(event.request, { ignoreSearch: true }).then((cached) => {
      return cached || fetch(event.request);
    })
  );
});
