# Drive Tracker

## Current Scope

This project is still based on the `Driving Tracker App` proposal in `references/Capstone Project Proposal.pdf`, but the sync backend is now implemented with `Supabase` instead of a custom `Ktor` service.

The app remains aligned with the course structure:

- Kotlin
- Jetpack Compose
- ViewModel-based UI state
- Room persistence
- repository pattern
- WorkManager background sync
- simple networking integration
- OpenRouteService reverse geocoding for trip addresses

## Implemented Features

- GPS trip recording
- trip distance, duration, average speed, and max speed tracking
- in-app route board drawn from recorded GPS points
- local Room storage
- trip history and detail screen
- weekly and monthly insights
- Supabase email/password authentication
- offline-first trip sync to Supabase when a user is signed in

## Project Structure

`app/src/main/java/com/charbel/drivetracker`

- `data/auth/` local session storage
- `data/local/` Room entities, DAO, and mappers
- `data/remote/` Supabase and OpenRouteService Retrofit services and DTOs
- `data/repository/` auth and trip repositories
- `data/sync/` WorkManager scheduling and Supabase sync service
- `navigation/` app routes and nav graph
- `tracking/` GPS trip tracker
- `ui/` Compose screens, components, theme, and ViewModels
- `util/` formatting, analytics, time, and permission helpers

## What You Need To Do

1. Create a Supabase project.
2. Run [supabase/setup.sql](</c:/Users/Charbel/Desktop/Mobile/supabase/setup.sql>) in the Supabase SQL Editor.
3. In `Authentication > Providers > Email`, keep email/password enabled.
4. For the easiest demo flow, disable `Confirm email`.
5. Add these values to `local.properties`:

```properties
SUPABASE_URL=https://your-project-ref.supabase.co
SUPABASE_ANON_KEY=your_anon_key
ORS_API_KEY=your_openrouteservice_api_key
```

6. Open the project in Android Studio and run it on a device or emulator with location enabled.

`ORS_API_KEY` is optional for the route board itself. The app will still render recorded routes without it, but OpenRouteService improves the saved start and end addresses.

## Verification

The Android app was verified with:

```powershell
.\gradlew.bat :app:compileDebugKotlin
```

That compile completed successfully after the Supabase auth and sync integration.
