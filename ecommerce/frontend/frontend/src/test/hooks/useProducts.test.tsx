import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { act, renderHook, waitFor } from '@testing-library/react'
import { afterEach, describe, expect, it, vi } from 'vitest'
import { api } from '../../api/axiosConfig'
import { useProducts } from '../../hooks/useProducts'

describe('useProducts', () => {
  afterEach(() => vi.restoreAllMocks())

  it('loads products and excludes empty filter values from request params', async () => {
    const get = vi.spyOn(api, 'get').mockResolvedValue({ data: [{ id: 1 }] })
    const client = new QueryClient({ defaultOptions: { queries: { retry: false } } })
    const wrapper = ({ children }: { children: React.ReactNode }) => (
      <QueryClientProvider client={client}>{children}</QueryClientProvider>
    )
    const { result } = renderHook(
      () => useProducts({ name: 'book', category: '', minPrice: undefined, maxPrice: 25 }),
      { wrapper },
    )

    await waitFor(() => expect(result.current.data).toEqual([{ id: 1 }]))
    expect(get).toHaveBeenCalledWith('/products', { params: { name: 'book', maxPrice: 25 } })
    act(() => client.clear())
  })

  it('exposes request failures as query errors', async () => {
    vi.spyOn(api, 'get').mockRejectedValue(new Error('offline'))
    const client = new QueryClient({ defaultOptions: { queries: { retry: false } } })
    const wrapper = ({ children }: { children: React.ReactNode }) => (
      <QueryClientProvider client={client}>{children}</QueryClientProvider>
    )
    const { result } = renderHook(() => useProducts({}), { wrapper })

    await waitFor(() => expect(result.current.isError).toBe(true))
    expect(result.current.error).toEqual(new Error('offline'))
    client.clear()
  })
})
