-- Tạo database
IF DB_ID('BaiTap5DB') IS NULL
BEGIN
    CREATE DATABASE BaiTap5DB;
END
GO

USE BaiTap5DB;
GO

-- Tạo bảng Category
CREATE TABLE Category (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(255) NOT NULL UNIQUE,
    description NVARCHAR(500),
    status BIT DEFAULT 1,
    created_date DATETIME2 DEFAULT GETDATE(),
    updated_date DATETIME2 DEFAULT GETDATE()
);

-- Tạo bảng User
CREATE TABLE [User] (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(50) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    email NVARCHAR(100) NOT NULL UNIQUE,
    full_name NVARCHAR(255),
    phone NVARCHAR(20),
    role NVARCHAR(20) DEFAULT 'USER' CHECK (role IN ('ADMIN', 'USER')),
    status BIT DEFAULT 1,
    created_date DATETIME2 DEFAULT GETDATE(),
    updated_date DATETIME2 DEFAULT GETDATE()
);

-- Tạo bảng Video
CREATE TABLE Video (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    title NVARCHAR(255) NOT NULL,
    description NVARCHAR(1000),
    video_url NVARCHAR(500),
    thumbnail_url NVARCHAR(500),
    duration INT, -- thời lượng video tính bằng giây
    views BIGINT DEFAULT 0,
    likes BIGINT DEFAULT 0,
    category_id BIGINT,
    user_id BIGINT,
    status BIT DEFAULT 1,
    updated_date DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (category_id) REFERENCES Category(id),
    FOREIGN KEY (user_id) REFERENCES [User](id)
);

    -- Thêm dữ liệu mẫu
    INSERT INTO Category (name, description) VALUES 
    ('Technology', 'Videos about technology and programming'),
    ('Education', 'Educational content and tutorials'),
    ('Entertainment', 'Entertainment videos and shows'),
    ('Music', 'Music videos and concerts'),
    ('Sports', 'Sports highlights and matches');
    
    -- Đảm bảo tài khoản admin luôn đúng (UPSERT)
    MERGE [User] AS target
    USING (SELECT 'admin' AS username) AS src
    ON (target.username = src.username)
    WHEN MATCHED THEN UPDATE SET 
        password = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.',
        email = 'admin@example.com',
        full_name = 'Administrator',
        role = 'ADMIN',
        status = 1
    WHEN NOT MATCHED THEN
        INSERT (username, password, email, full_name, role, status)
        VALUES ('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'admin@example.com', 'Administrator', 'ADMIN', 1);
    
    -- Chuẩn hóa password (loại bỏ khoảng trắng vô tình)
    UPDATE [User]
    SET password = LTRIM(RTRIM(password))
    WHERE username = 'admin';
    
    -- Seed thêm user mẫu nếu chưa tồn tại
    IF NOT EXISTS (SELECT 1 FROM [User] WHERE username = 'user1')
    BEGIN
        INSERT INTO [User] (username, password, email, full_name, role, status) VALUES 
        ('user1', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'user1@example.com', 'John Doe', 'USER', 1)
    END
    IF NOT EXISTS (SELECT 1 FROM [User] WHERE username = 'user2')
    BEGIN
        INSERT INTO [User] (username, password, email, full_name, role, status) VALUES 
        ('user2', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'user2@example.com', 'Jane Smith', 'USER', 1)
    END

INSERT INTO Video (title, description, video_url, thumbnail_url, duration, category_id, user_id) VALUES 
('Java Programming Tutorial', 'Learn Java programming from basics', 'https://example.com/video1.mp4', 'https://example.com/thumb1.jpg', 1800, 1, 2),
('Spring Boot Introduction', 'Introduction to Spring Boot framework', 'https://example.com/video2.mp4', 'https://example.com/thumb2.jpg', 2400, 1, 2),
('Database Design Principles', 'Learn how to design efficient databases', 'https://example.com/video3.mp4', 'https://example.com/thumb3.jpg', 3600, 2, 3),
('Music Video Collection', 'Best music videos of 2024', 'https://example.com/video4.mp4', 'https://example.com/thumb4.jpg', 240, 4, 3);

-- Tạo index để tối ưu hóa tìm kiếm
CREATE INDEX IX_Category_Name ON Category(name);
CREATE INDEX IX_User_Username ON [User](username);
CREATE INDEX IX_User_Email ON [User](email);
CREATE INDEX IX_Video_Title ON Video(title);
CREATE INDEX IX_Video_Category ON Video(category_id);
CREATE INDEX IX_Video_User ON Video(user_id);

GO
