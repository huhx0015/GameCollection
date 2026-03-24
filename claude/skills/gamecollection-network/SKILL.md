---
name: gamecollection-network
description: Network and Retrofit setup for Game Collection. Use when adding API endpoints, Retrofit interfaces, or configuring network layer. Uses Retrofit, OkHttp, Moshi.
---

# Game Collection Network

## Retrofit Interface

Define API endpoints in `core/network/src/main/kotlin/com/huhx0015/gamecollection/network/interfaces/RetrofitInterface.kt`. Extend or use this interface for your API calls.

## NetworkModule

`core/network/.../injections/NetworkModule.kt` provides:

- `Retrofit` with base URL from `AndroidConstants.API_URL`
- `Moshi` with `KotlinJsonAdapterFactory`
- `OkHttpClient` with cache (10MB) and `HttpLoggingInterceptor` (BODY)
- `RetrofitInterface` via `retrofit.create(RetrofitInterface::class.java)`

## Usage

Inject `Retrofit` or `RetrofitInterface` in activities/fragments/ViewModels. Add endpoint methods to `RetrofitInterface` (or create interfaces and obtain via `retrofit.create()`).
