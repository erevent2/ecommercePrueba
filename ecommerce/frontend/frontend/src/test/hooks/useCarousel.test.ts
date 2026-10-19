import { act, renderHook } from '@testing-library/react'
import { afterEach, describe, expect, it, vi } from 'vitest'
import { useCarousel } from '../../hooks/useCarousel'

describe('useCarousel', () => {
  afterEach(() => vi.useRealTimers())

  it('cycles through available items and cleans up its timer', () => {
    vi.useFakeTimers()
    const { result, unmount } = renderHook(() => useCarousel(3, 100))

    expect(result.current).toBe(0)
    act(() => vi.advanceTimersByTime(100))
    expect(result.current).toBe(1)
    act(() => vi.advanceTimersByTime(200))
    expect(result.current).toBe(0)
    unmount()
    expect(vi.getTimerCount()).toBe(0)
  })

  it('does not schedule a timer for zero or one item', () => {
    vi.useFakeTimers()
    const { result, rerender } = renderHook(({ length }) => useCarousel(length), {
      initialProps: { length: 1 },
    })

    expect(result.current).toBe(0)
    expect(vi.getTimerCount()).toBe(0)
    rerender({ length: 0 })
    expect(vi.getTimerCount()).toBe(0)
  })
})
