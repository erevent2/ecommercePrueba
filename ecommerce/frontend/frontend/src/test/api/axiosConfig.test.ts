import { afterEach, describe, expect, it } from 'vitest'
import { api } from '../../api/axiosConfig'

describe('api request configuration', () => {
  afterEach(() => localStorage.clear())

  it('adds the stored bearer token to requests', async () => {
    localStorage.setItem('blossom_token', 'session-token')
    let authorization: unknown

    await api.get('/example', {
      adapter: async (config) => {
        authorization = config.headers.Authorization
        return { data: {}, status: 200, statusText: 'OK', headers: {}, config }
      },
    })

    expect(authorization).toBe('Bearer session-token')
  })

  it('leaves authorization unset when no token exists', async () => {
    let authorization: unknown

    await api.get('/example', {
      adapter: async (config) => {
        authorization = config.headers.Authorization
        return { data: {}, status: 200, statusText: 'OK', headers: {}, config }
      },
    })

    expect(authorization).toBeUndefined()
  })
})
