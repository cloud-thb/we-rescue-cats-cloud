terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = "us-east-1"
}

# --- Datenquellen, um Werte automatisch zu finden ---
data "aws_ami" "latest_ubuntu" {
  most_recent = true
  owners      = ["099720109477"] 
  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-jammy-22.04-amd64-server-*"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}

data "aws_subnet" "alb_a" {
  id = "subnet-07ae812c246f41e72"
}

data "aws_subnet" "alb_b" {
  id = "subnet-0198bd9fb9a9d05b7"
}

# ============== EC2 Instanz ==============
resource "aws_instance" "backend" {
  ami                    = data.aws_ami.latest_ubuntu.id // Benutzt jetzt die automatisch gefundene, neueste Ubuntu-ID
  instance_type          = "t3.micro"
  key_name               = "semester-projekt-key"
  vpc_security_group_ids = ["sg-098e335a115876898"]
  
  tags = {
    Name = "semester-projekt-server"
  }
}

# ============== RDS Datenbank ==============
resource "aws_db_instance" "main" {
  identifier               = "semesterprojekt-db"
  engine                   = "mysql"
  engine_version           = "8.0"                   
  instance_class           = "db.t3.micro"           
  allocated_storage        = 20
  storage_type             = "gp2"
  username                 = "admin"
  password                 = var.db_password
  vpc_security_group_ids   = ["sg-0b6f166b2a4a4a018"]
  skip_final_snapshot      = true
  publicly_accessible      = true
  multi_az                 = false

  tags = {
    Name = "semesterprojekt-db"
  }
}

# ============== S3 Bucket ==============
# ... (Der S3-Teil kann unverändert bleiben) ...
resource "aws_s3_bucket" "frontend" {
  bucket = "cloud-semesterprojekt-2026"
  tags = {
    Name = "Frontend Hosting"
  }
}

resource "aws_s3_bucket_website_configuration" "frontend" {
  bucket = aws_s3_bucket.frontend.id
  index_document {
    suffix = "index.html"
  }
  error_document {
    key = "index.html"
  }
}

resource "aws_s3_bucket_public_access_block" "frontend" {
  bucket                  = aws_s3_bucket.frontend.id
  block_public_acls       = false
  block_public_policy     = false
  ignore_public_acls      = false
  restrict_public_buckets = false
}


# ============== Load Balancer ==============
resource "aws_lb" "main" {
  name               = "semester-projekt-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = ["sg-098e335a115876898"]
  subnets            = ["subnet-07ae812c246f41e72", "subnet-0198bd9fb9a9d05b7"]

  tags = {
    Name = "semester-projekt-alb"
  }
}

resource "aws_lb_target_group" "backend" {
  name        = "semester-projekt-backend"
  port        = 8080
  protocol    = "HTTP"
  vpc_id      = data.aws_subnet.alb_a.vpc_id
  target_type = "instance"

  health_check {
    path                = "/"
    protocol            = "HTTP"
    matcher             = "200-499"
    interval            = 30
    timeout             = 5
    healthy_threshold   = 2
    unhealthy_threshold = 2
  }
}

resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.main.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.backend.arn
  }
}

resource "aws_lb_target_group_attachment" "backend" {
  target_group_arn = aws_lb_target_group.backend.arn
  target_id        = aws_instance.backend.id
  port             = 8080
}
