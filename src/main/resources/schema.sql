-- Drop child tables first
DROP TABLE IF EXISTS application_material;
DROP TABLE IF EXISTS template;
DROP TABLE IF EXISTS template_folder;
DROP TABLE IF EXISTS job_posting;
DROP TABLE IF EXISTS company;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS websites;
DROP TABLE IF EXISTS awards_certs;
DROP TABLE IF EXISTS skill;
DROP TABLE IF EXISTS education;
DROP TABLE IF EXISTS experience;

-- Drop parent table last
DROP TABLE IF EXISTS user_profile;

-- User and Profile Core
CREATE TABLE user_profile (
    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE,
    password_hash TEXT NOT NULL,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    phone TEXT,
    address_line_1 TEXT,
    address_line_2 TEXT,
    city TEXT,
    state TEXT,
    zip TEXT,
    summary TEXT,
    ollama_api_key TEXT
);

CREATE TABLE experience (
    experience_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    job_title TEXT NOT NULL,
    company_name TEXT NOT NULL,
    location TEXT,
    start_date TEXT,
    end_date TEXT,
    description TEXT,
    display_order INTEGER DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES user_profile(user_id) ON DELETE CASCADE
);

CREATE TABLE education (
    education_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    institution TEXT NOT NULL,
    degree TEXT,
    start_date TEXT,
    end_date TEXT,
    gpa TEXT,
    FOREIGN KEY (user_id) REFERENCES user_profile(user_id) ON DELETE CASCADE
);

CREATE TABLE skill (
    skill_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    skill_name TEXT NOT NULL,
    proficiency_level TEXT,
    FOREIGN KEY (user_id) REFERENCES user_profile(user_id) ON DELETE CASCADE
);

CREATE TABLE awards_certs (
    award_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    title TEXT NOT NULL,
    issuer TEXT,
    date_issued TEXT,
    description TEXT,
    FOREIGN KEY (user_id) REFERENCES user_profile(user_id) ON DELETE CASCADE
);

CREATE TABLE websites (
    website_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    platform TEXT NOT NULL, 
    url TEXT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user_profile(user_id) ON DELETE CASCADE
);

CREATE TABLE projects (
    project_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    url TEXT,
    FOREIGN KEY (user_id) REFERENCES user_profile(user_id) ON DELETE CASCADE
);

-- Template Management Core
CREATE TABLE template_folder (
    folder_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    folder_name TEXT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user_profile(user_id) ON DELETE CASCADE
);

CREATE TABLE template (
    template_id INTEGER PRIMARY KEY AUTOINCREMENT,
    folder_id INTEGER,
    user_id INTEGER NOT NULL,
    template_name TEXT NOT NULL,
    template_type TEXT NOT NULL, 
    file_loc TEXT NOT NULL,
    FOREIGN KEY (folder_id) REFERENCES template_folder(folder_id) ON DELETE SET NULL,
    FOREIGN KEY (user_id) REFERENCES user_profile(user_id) ON DELETE CASCADE
);

-- Job Tracking Core
CREATE TABLE company (
    company_id INTEGER PRIMARY KEY AUTOINCREMENT,
    company_name TEXT NOT NULL UNIQUE,
    industry TEXT,
    description TEXT,
    website_url TEXT
);

CREATE TABLE job_posting (
    jobpost_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    company_id INTEGER,
    company_name TEXT, 
    job_title TEXT NOT NULL,
    location TEXT,
    job_url TEXT,
    pay_min REAL,
    pay_max REAL,
    pay_period TEXT,
    source_file_name TEXT,
    responsibilities TEXT,
    requirements TEXT,
    nice_to_have TEXT,
    benefits TEXT,
    raw_text TEXT,
    normalized_text TEXT,
    match_score REAL,
    spam_score REAL,
    status TEXT DEFAULT 'Saved', 
    date_added TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user_profile(user_id) ON DELETE CASCADE,
    FOREIGN KEY (company_id) REFERENCES company(company_id)
);

CREATE TABLE application_material (
    material_id INTEGER PRIMARY KEY AUTOINCREMENT,
    jobpost_id INTEGER NOT NULL,
    template_id INTEGER,
    material_type TEXT NOT NULL, 
    file_loc TEXT NOT NULL,
    generation_date TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (jobpost_id) REFERENCES job_posting(jobpost_id) ON DELETE CASCADE,
    FOREIGN KEY (template_id) REFERENCES template(template_id) ON DELETE SET NULL
);