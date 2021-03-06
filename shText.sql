USE [master]
GO
/****** Object:  Database [stext]    Script Date: 2019/6/21 17:40:12 ******/
CREATE DATABASE [stext]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'stext', FILENAME = N'd:\Microsoft SQL Server\MSSQL14.MSSQLSERVER\MSSQL\DATA\stext.mdf' , SIZE = 8192KB , MAXSIZE = UNLIMITED, FILEGROWTH = 65536KB )
 LOG ON 
( NAME = N'stext_log', FILENAME = N'd:\Microsoft SQL Server\MSSQL14.MSSQLSERVER\MSSQL\DATA\stext_log.ldf' , SIZE = 8192KB , MAXSIZE = 2048GB , FILEGROWTH = 65536KB )
GO
ALTER DATABASE [stext] SET COMPATIBILITY_LEVEL = 140
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [stext].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [stext] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [stext] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [stext] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [stext] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [stext] SET ARITHABORT OFF 
GO
ALTER DATABASE [stext] SET AUTO_CLOSE OFF 
GO
ALTER DATABASE [stext] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [stext] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [stext] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [stext] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [stext] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [stext] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [stext] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [stext] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [stext] SET  ENABLE_BROKER 
GO
ALTER DATABASE [stext] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [stext] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [stext] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [stext] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [stext] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [stext] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [stext] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [stext] SET RECOVERY FULL 
GO
ALTER DATABASE [stext] SET  MULTI_USER 
GO
ALTER DATABASE [stext] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [stext] SET DB_CHAINING OFF 
GO
ALTER DATABASE [stext] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [stext] SET TARGET_RECOVERY_TIME = 60 SECONDS 
GO
ALTER DATABASE [stext] SET DELAYED_DURABILITY = DISABLED 
GO
EXEC sys.sp_db_vardecimal_storage_format N'stext', N'ON'
GO
ALTER DATABASE [stext] SET QUERY_STORE = OFF
GO
USE [stext]
GO
/****** Object:  Table [dbo].[book]    Script Date: 2019/6/21 17:40:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[book](
	[bid] [int] NOT NULL,
	[btitle] [nvarchar](15) NULL,
	[bcount] [int] NULL,
	[bcharNum] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[bid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[btitle] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[chapter]    Script Date: 2019/6/21 17:40:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[chapter](
	[cid] [int] NOT NULL,
	[ctitle] [nvarchar](15) NULL,
	[cseq] [int] NULL,
	[ccharNum] [int] NULL,
	[ccontent] [text] NULL,
	[bid] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[cid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[role]    Script Date: 2019/6/21 17:40:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[role](
	[rid] [int] NOT NULL,
	[rname] [nvarchar](15) NOT NULL,
	[rsex] [nvarchar](10) NULL,
	[rcontent] [nvarchar](1000) NULL,
	[bid] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[rid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[role_record]    Script Date: 2019/6/21 17:40:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[role_record](
	[rid] [int] NULL,
	[cid] [int] NULL
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[user_info]    Script Date: 2019/6/21 17:40:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[user_info](
	[uid] [int] NOT NULL,
	[act] [varchar](20) NOT NULL,
	[psw] [varchar](20) NOT NULL,
	[uname] [nvarchar](15) NULL,
PRIMARY KEY CLUSTERED 
(
	[uid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[user_permission]    Script Date: 2019/6/21 17:40:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[user_permission](
	[bid] [int] NULL,
	[uid] [int] NULL,
	[permission] [int] NULL
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[world]    Script Date: 2019/6/21 17:40:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[world](
	[wid] [int] NOT NULL,
	[wname] [nvarchar](20) NOT NULL,
	[wcontent] [nvarchar](1000) NULL,
	[bid] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[wid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[world_record]    Script Date: 2019/6/21 17:40:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[world_record](
	[wid] [int] NULL,
	[cid] [int] NULL
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[book] ADD  CONSTRAINT [DF_book_bcount]  DEFAULT ((0)) FOR [bcount]
GO
ALTER TABLE [dbo].[book] ADD  CONSTRAINT [DF_book_bcharNum]  DEFAULT ((0)) FOR [bcharNum]
GO
ALTER TABLE [dbo].[chapter] ADD  CONSTRAINT [DF_chapter_ccharNum]  DEFAULT ((0)) FOR [ccharNum]
GO
ALTER TABLE [dbo].[chapter]  WITH CHECK ADD FOREIGN KEY([bid])
REFERENCES [dbo].[book] ([bid])
GO
ALTER TABLE [dbo].[role]  WITH CHECK ADD FOREIGN KEY([bid])
REFERENCES [dbo].[book] ([bid])
GO
ALTER TABLE [dbo].[role_record]  WITH CHECK ADD FOREIGN KEY([cid])
REFERENCES [dbo].[chapter] ([cid])
GO
ALTER TABLE [dbo].[role_record]  WITH CHECK ADD FOREIGN KEY([rid])
REFERENCES [dbo].[role] ([rid])
GO
ALTER TABLE [dbo].[user_permission]  WITH CHECK ADD FOREIGN KEY([bid])
REFERENCES [dbo].[book] ([bid])
GO
ALTER TABLE [dbo].[user_permission]  WITH CHECK ADD FOREIGN KEY([uid])
REFERENCES [dbo].[user_info] ([uid])
GO
ALTER TABLE [dbo].[world]  WITH CHECK ADD FOREIGN KEY([bid])
REFERENCES [dbo].[book] ([bid])
GO
ALTER TABLE [dbo].[world_record]  WITH CHECK ADD FOREIGN KEY([cid])
REFERENCES [dbo].[chapter] ([cid])
GO
ALTER TABLE [dbo].[world_record]  WITH CHECK ADD FOREIGN KEY([wid])
REFERENCES [dbo].[world] ([wid])
GO
ALTER TABLE [dbo].[chapter]  WITH CHECK ADD CHECK  (([cseq]>(0)))
GO
ALTER TABLE [dbo].[user_permission]  WITH CHECK ADD CHECK  (([permission]>(0) AND [permission]<(4)))
GO
/****** Object:  Trigger [dbo].[deleteCharNum]    Script Date: 2019/6/21 17:40:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
create trigger [dbo].[deleteCharNum]
on [dbo].[chapter]
after delete
as
begin
declare @c int,@allnum int
begin
	select @c=count(chapter.cid) from chapter,deleted where chapter.bid=deleted.bid;
	select @allnum=sum(chapter.ccharNum) from chapter,deleted where chapter.bid=deleted.bid;
	update book set bcount=@c,bcharNum=@allnum from book,deleted where deleted.bid=book.bid;
end
end


GO
ALTER TABLE [dbo].[chapter] ENABLE TRIGGER [deleteCharNum]
GO
/****** Object:  Trigger [dbo].[updateCharNum]    Script Date: 2019/6/21 17:40:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
create trigger [dbo].[updateCharNum]
on [dbo].[chapter]
after insert,update
as
begin
declare @c int,@allnum int
begin
	select @c=count(chapter.cid) from chapter,inserted where chapter.bid=inserted.bid;
	select @allnum=sum(chapter.ccharNum) from chapter,inserted where chapter.bid=inserted.bid;
	update book set bcount=@c,bcharNum=@allnum from book,inserted where inserted.bid=book.bid;
end
end


GO
ALTER TABLE [dbo].[chapter] ENABLE TRIGGER [updateCharNum]
GO
USE [master]
GO
ALTER DATABASE [stext] SET  READ_WRITE 
GO
