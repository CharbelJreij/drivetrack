create extension if not exists pgcrypto;

create table if not exists public.trips (
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null default auth.uid() references auth.users(id) on delete cascade,
    client_local_id bigint not null,
    title text not null,
    started_at_millis bigint not null,
    ended_at_millis bigint not null,
    distance_meters double precision not null,
    duration_seconds bigint not null,
    average_speed_kmh double precision not null,
    max_speed_kmh double precision not null,
    start_address text,
    end_address text,
    created_at timestamp with time zone not null default now(),
    unique (user_id, client_local_id)
);

create index if not exists trips_user_started_idx
    on public.trips(user_id, started_at_millis desc);

create table if not exists public.trip_points (
    id bigint generated always as identity primary key,
    trip_id uuid not null references public.trips(id) on delete cascade,
    sequence_index integer not null,
    latitude double precision not null,
    longitude double precision not null,
    recorded_at_millis bigint not null,
    speed_meters_per_second real not null,
    unique (trip_id, sequence_index)
);

create index if not exists trip_points_trip_sequence_idx
    on public.trip_points(trip_id, sequence_index);

alter table public.trips enable row level security;
alter table public.trip_points enable row level security;

drop policy if exists "Users can read their own trips" on public.trips;
create policy "Users can read their own trips"
on public.trips
for select
to authenticated
using (
    (select auth.uid()) is not null
    and (select auth.uid()) = user_id
);

drop policy if exists "Users can insert their own trips" on public.trips;
create policy "Users can insert their own trips"
on public.trips
for insert
to authenticated
with check (
    (select auth.uid()) is not null
    and (select auth.uid()) = user_id
);

drop policy if exists "Users can delete their own trips" on public.trips;
create policy "Users can delete their own trips"
on public.trips
for delete
to authenticated
using (
    (select auth.uid()) is not null
    and (select auth.uid()) = user_id
);

drop policy if exists "Users can read their own trip points" on public.trip_points;
create policy "Users can read their own trip points"
on public.trip_points
for select
to authenticated
using (
    exists (
        select 1
        from public.trips
        where trips.id = trip_points.trip_id
          and trips.user_id = (select auth.uid())
    )
);

drop policy if exists "Users can insert their own trip points" on public.trip_points;
create policy "Users can insert their own trip points"
on public.trip_points
for insert
to authenticated
with check (
    exists (
        select 1
        from public.trips
        where trips.id = trip_points.trip_id
          and trips.user_id = (select auth.uid())
    )
);

drop policy if exists "Users can delete their own trip points" on public.trip_points;
create policy "Users can delete their own trip points"
on public.trip_points
for delete
to authenticated
using (
    exists (
        select 1
        from public.trips
        where trips.id = trip_points.trip_id
          and trips.user_id = (select auth.uid())
    )
);
