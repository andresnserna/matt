-- User and Profile Core
CREATE TABLE user_profile (
    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    email TEXT,
    phone TEXT,
    summary TEXT
);

CREATE TABLE experience (
    experience_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    job_title TEXT NOT NULL,
    company_name TEXT NOT NULL,
    start_date TEXT,
    end_date TEXT,
    description TEXT,
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
    platform TEXT NOT NULL, -- e.g., LinkedIn, GitHub, Portfolio
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
    company_id INTEGER NOT NULL,
    job_title TEXT NOT NULL,
    job_url TEXT,
    job_description TEXT,
    match_score REAL,
    spam_score REAL,
    status TEXT DEFAULT 'Saved', -- e.g., Saved, Applied, Interviewing, Rejected
    date_added TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user_profile(user_id) ON DELETE CASCADE,
    FOREIGN KEY (company_id) REFERENCES company(company_id)
);

CREATE TABLE application_material (
    material_id INTEGER PRIMARY KEY AUTOINCREMENT,
    jobpost_id INTEGER NOT NULL,
    material_type TEXT NOT NULL, -- e.g., Resume, Cover Letter, Outreach
    file_loc TEXT NOT NULL,
    generation_date TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (jobpost_id) REFERENCES job_posting(jobpost_id) ON DELETE CASCADE
);