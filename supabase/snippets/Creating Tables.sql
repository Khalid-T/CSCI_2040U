CREATE TABLE users (
  user_id UUID PRIMARY KEY REFERENCES auth.users(id),
  username TEXT,
  role TEXT DEFAULT 'user'
);

CREATE TABLE flora (
  flora_id INT PRIMARY KEY,
  common_name TEXT,
  sci_name TEXT,
  family TEXT,
  genus TEXT,
  species_epithet TEXT,
  care_level TEXT,
  watering TEXT,
  origin TEXT,
  description TEXT,
  image_url TEXT
);

CREATE TABLE saved_list (
  saved_id SERIAL PRIMARY KEY,
  user_id UUID REFERENCES users(user_id),
  flora_id INT REFERENCES flora(flora_id)
);