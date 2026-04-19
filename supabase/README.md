# Supabase Setup

Run [setup.sql](</c:/Users/Charbel/Desktop/Mobile/supabase/setup.sql>) in the Supabase SQL Editor for your project.

After that:

1. Open `Authentication > Providers > Email`.
2. Keep email/password enabled.
3. For the easiest course demo, disable `Confirm email`.
4. Open `Project Settings > API`.
5. Copy the `Project URL`.
6. Copy the `anon` key.
7. Add both values to `local.properties`:

```properties
SUPABASE_URL=https://your-project-ref.supabase.co
SUPABASE_ANON_KEY=your_anon_key
```

The Android app already:

- stores trips locally in Room
- lets users register and sign in
- keeps the current session on device
- uploads pending trips with WorkManager when a user is signed in
