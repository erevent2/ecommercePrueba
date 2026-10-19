import { useState, useEffect } from 'react';

export const useCarousel = (itemsLength: number, intervalMs: number = 3000) => {
  const [currentIndex, setCurrentIndex] = useState(0);

  useEffect(() => {
    if (itemsLength <= 1) return; // No iterar si hay 1 o 0 imágenes

    const timer = setInterval(() => {
      setCurrentIndex((prevIndex) => (prevIndex + 1) % itemsLength);
    }, intervalMs);

    // Cleanup crucial para evitar fugas de memoria (memory leaks)
    return () => clearInterval(timer);
  }, [itemsLength, intervalMs]);

  return currentIndex;
};