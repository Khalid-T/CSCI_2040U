create sequence "public"."saved_list_saved_id_seq";


  create table "public"."flora" (
    "flora_id" integer not null,
    "common_name" text,
    "sci_name" text,
    "family" text,
    "genus" text,
    "species_epithet" text,
    "care_level" text,
    "watering" text,
    "origin" text,
    "description" text,
    "image_url" text
      );



  create table "public"."saved_list" (
    "saved_id" integer not null default nextval('public.saved_list_saved_id_seq'::regclass),
    "user_id" uuid,
    "flora_id" integer
      );



  create table "public"."users" (
    "user_id" uuid not null,
    "username" text,
    "role" text default 'user'::text
      );


alter sequence "public"."saved_list_saved_id_seq" owned by "public"."saved_list"."saved_id";

CREATE UNIQUE INDEX flora_pkey ON public.flora USING btree (flora_id);

CREATE UNIQUE INDEX saved_list_pkey ON public.saved_list USING btree (saved_id);

CREATE UNIQUE INDEX users_pkey ON public.users USING btree (user_id);

alter table "public"."flora" add constraint "flora_pkey" PRIMARY KEY using index "flora_pkey";

alter table "public"."saved_list" add constraint "saved_list_pkey" PRIMARY KEY using index "saved_list_pkey";

alter table "public"."users" add constraint "users_pkey" PRIMARY KEY using index "users_pkey";

alter table "public"."saved_list" add constraint "saved_list_flora_id_fkey" FOREIGN KEY (flora_id) REFERENCES public.flora(flora_id) not valid;

alter table "public"."saved_list" validate constraint "saved_list_flora_id_fkey";

alter table "public"."saved_list" add constraint "saved_list_user_id_fkey" FOREIGN KEY (user_id) REFERENCES public.users(user_id) not valid;

alter table "public"."saved_list" validate constraint "saved_list_user_id_fkey";

alter table "public"."users" add constraint "users_user_id_fkey" FOREIGN KEY (user_id) REFERENCES auth.users(id) not valid;

alter table "public"."users" validate constraint "users_user_id_fkey";

grant delete on table "public"."flora" to "anon";

grant insert on table "public"."flora" to "anon";

grant references on table "public"."flora" to "anon";

grant select on table "public"."flora" to "anon";

grant trigger on table "public"."flora" to "anon";

grant truncate on table "public"."flora" to "anon";

grant update on table "public"."flora" to "anon";

grant delete on table "public"."flora" to "authenticated";

grant insert on table "public"."flora" to "authenticated";

grant references on table "public"."flora" to "authenticated";

grant select on table "public"."flora" to "authenticated";

grant trigger on table "public"."flora" to "authenticated";

grant truncate on table "public"."flora" to "authenticated";

grant update on table "public"."flora" to "authenticated";

grant delete on table "public"."flora" to "service_role";

grant insert on table "public"."flora" to "service_role";

grant references on table "public"."flora" to "service_role";

grant select on table "public"."flora" to "service_role";

grant trigger on table "public"."flora" to "service_role";

grant truncate on table "public"."flora" to "service_role";

grant update on table "public"."flora" to "service_role";

grant delete on table "public"."saved_list" to "anon";

grant insert on table "public"."saved_list" to "anon";

grant references on table "public"."saved_list" to "anon";

grant select on table "public"."saved_list" to "anon";

grant trigger on table "public"."saved_list" to "anon";

grant truncate on table "public"."saved_list" to "anon";

grant update on table "public"."saved_list" to "anon";

grant delete on table "public"."saved_list" to "authenticated";

grant insert on table "public"."saved_list" to "authenticated";

grant references on table "public"."saved_list" to "authenticated";

grant select on table "public"."saved_list" to "authenticated";

grant trigger on table "public"."saved_list" to "authenticated";

grant truncate on table "public"."saved_list" to "authenticated";

grant update on table "public"."saved_list" to "authenticated";

grant delete on table "public"."saved_list" to "service_role";

grant insert on table "public"."saved_list" to "service_role";

grant references on table "public"."saved_list" to "service_role";

grant select on table "public"."saved_list" to "service_role";

grant trigger on table "public"."saved_list" to "service_role";

grant truncate on table "public"."saved_list" to "service_role";

grant update on table "public"."saved_list" to "service_role";

grant delete on table "public"."users" to "anon";

grant insert on table "public"."users" to "anon";

grant references on table "public"."users" to "anon";

grant select on table "public"."users" to "anon";

grant trigger on table "public"."users" to "anon";

grant truncate on table "public"."users" to "anon";

grant update on table "public"."users" to "anon";

grant delete on table "public"."users" to "authenticated";

grant insert on table "public"."users" to "authenticated";

grant references on table "public"."users" to "authenticated";

grant select on table "public"."users" to "authenticated";

grant trigger on table "public"."users" to "authenticated";

grant truncate on table "public"."users" to "authenticated";

grant update on table "public"."users" to "authenticated";

grant delete on table "public"."users" to "service_role";

grant insert on table "public"."users" to "service_role";

grant references on table "public"."users" to "service_role";

grant select on table "public"."users" to "service_role";

grant trigger on table "public"."users" to "service_role";

grant truncate on table "public"."users" to "service_role";

grant update on table "public"."users" to "service_role";


