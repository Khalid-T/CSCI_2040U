INSERT INTO public.users (user_id, username, role)
SELECT id, email, 'user' FROM auth.users;