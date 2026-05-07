-- ============================================================
-- Federation of Agricultural Collectivities - PostgreSQL DDL
-- Generated from MCD v2.0
-- ============================================================

-- ------------------------------------------------------------
-- DROP TABLES (reverse order of dependencies)
-- ------------------------------------------------------------

DROP TABLE IF EXISTS attendance CASCADE;
DROP TABLE IF EXISTS activity CASCADE;
DROP TABLE IF EXISTS "transaction" CASCADE;
DROP TABLE IF EXISTS account_mobile CASCADE;
DROP TABLE IF EXISTS account_extended CASCADE;
DROP TABLE IF EXISTS account CASCADE;
DROP TABLE IF EXISTS contribution CASCADE;
DROP TABLE IF EXISTS sponsorship CASCADE;
DROP TABLE IF EXISTS membership_history CASCADE;
DROP TABLE IF EXISTS federation_term CASCADE;
DROP TABLE IF EXISTS collectivity_term CASCADE;
DROP TABLE IF EXISTS federation_vote CASCADE;
DROP TABLE IF EXISTS collectivity_vote CASCADE;
DROP TABLE IF EXISTS position CASCADE;
DROP TABLE IF EXISTS collectivity CASCADE;
DROP TABLE IF EXISTS member CASCADE;
DROP TABLE IF EXISTS federation CASCADE;
DROP TABLE IF EXISTS collectivity_occupation CASCADE;
DROP TABLE IF EXISTS activity_occupation CASCADE;

-- ------------------------------------------------------------
-- Table: federation
-- Description: Top-level organization grouping agricultural collectivities
-- ------------------------------------------------------------
CREATE TABLE federation (
                             id VARCHAR(20) PRIMARY KEY,
                             name VARCHAR(150) NOT NULL
);

-- ------------------------------------------------------------
-- Table: member
-- Description: Person who is a member of a collectivity
-- ------------------------------------------------------------
CREATE TABLE member (
                         id VARCHAR(20) PRIMARY KEY,
                         lastname VARCHAR(100) NOT NULL,
                         firstname VARCHAR(250) NOT NULL,
                         birth_date DATE NOT NULL,
                         gender VARCHAR(10) NOT NULL CHECK (gender IN ('MALE', 'FEMALE')),
                         address VARCHAR(255) NOT NULL,
                         occupation VARCHAR(100) NOT NULL,
                         phone VARCHAR(20) NOT NULL,
                         email VARCHAR(100) UNIQUE NOT NULL,
                         membership_date DATE NOT NULL
);

-- ------------------------------------------------------------
-- Table: collectivity
-- Description: Local agricultural collectivity affiliated to federation
-- ------------------------------------------------------------
CREATE TABLE collectivity (
                               id VARCHAR(20) PRIMARY KEY,
                               number VARCHAR(20) UNIQUE,
                               name VARCHAR(150) UNIQUE,
                               specialty VARCHAR(100),
                               city VARCHAR(100),
                               creation_date DATE NOT NULL,
                               federation_id VARCHAR(20) NOT NULL,
                               status VARCHAR(20) DEFAULT 'PENDING' NOT NULL CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
                               authorized_by VARCHAR(20),
                               authorization_date DATE,
                               rejection_reason VARCHAR(255),
                               CONSTRAINT collectivity_federation_FK FOREIGN KEY (federation_id) REFERENCES federation(id) ON DELETE CASCADE,
                               CONSTRAINT collectivity_member_FK FOREIGN KEY (authorized_by) REFERENCES member(id) ON DELETE SET NULL
);

-- ------------------------------------------------------------
-- Table: position
-- Description: Role held by a member (president, treasurer, etc.)
-- ------------------------------------------------------------
CREATE TABLE position (
                           id VARCHAR(20) PRIMARY KEY,
                           label VARCHAR(30) NOT NULL CHECK (label IN ('PRESIDENT', 'VICE_PRESIDENT', 'TREASURER', 'SECRETARY', 'CONFIRMED_MEMBER', 'JUNIOR_MEMBER')),
                           context VARCHAR(20) NOT NULL CHECK (context IN ('BOTH', 'COLLECTIVITY', 'FEDERATION'))
);

-- ------------------------------------------------------------
-- Table: collectivity_vote
-- Description: Annual election of collectivity bureau
-- ------------------------------------------------------------
CREATE TABLE collectivity_vote (
                                    id VARCHAR(20) PRIMARY KEY,
                                    vote_date DATE NOT NULL,
                                    target_year INTEGER NOT NULL,
                                    collectivity_id VARCHAR(20) NOT NULL,
                                    CONSTRAINT collectivity_vote_collectivity_FK FOREIGN KEY (collectivity_id) REFERENCES collectivity(id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Table: federation_vote
-- Description: Annual election of federation bureau
-- ------------------------------------------------------------
CREATE TABLE federation_vote (
                                  id VARCHAR(20) PRIMARY KEY,
                                  vote_date DATE NOT NULL,
                                  target_year INTEGER NOT NULL,
                                  federation_id VARCHAR(20) NOT NULL,
                                  CONSTRAINT federation_vote_federation_FK FOREIGN KEY (federation_id) REFERENCES federation(id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Table: collectivity_term
-- Description: Term of a member within a collectivity
-- ------------------------------------------------------------
CREATE TABLE collectivity_term (
                                    id VARCHAR(20) PRIMARY KEY,
                                    year INTEGER NOT NULL,
                                    member_id VARCHAR(20) NOT NULL,
                                    collectivity_id VARCHAR(20) NOT NULL,
                                    position_id VARCHAR(20) NOT NULL,
                                    vote_id VARCHAR(20) NOT NULL,
                                    CONSTRAINT collectivity_term_member_FK FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
                                    CONSTRAINT collectivity_term_collectivity_FK FOREIGN KEY (collectivity_id) REFERENCES collectivity(id) ON DELETE CASCADE,
                                    CONSTRAINT collectivity_term_position_FK FOREIGN KEY (position_id) REFERENCES position(id) ON DELETE CASCADE,
                                    CONSTRAINT collectivity_term_vote_FK FOREIGN KEY (vote_id) REFERENCES collectivity_vote(id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Table: federation_term
-- Description: Term of a member within the federation
-- ------------------------------------------------------------
CREATE TABLE federation_term (
                                  id VARCHAR(20) PRIMARY KEY,
                                  start_year INTEGER NOT NULL,
                                  end_year INTEGER NOT NULL,
                                  member_id VARCHAR(20) NOT NULL,
                                  federation_id VARCHAR(20) NOT NULL,
                                  position_id VARCHAR(20) NOT NULL,
                                  vote_id VARCHAR(20) NOT NULL,
                                  CONSTRAINT federation_term_member_FK FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
                                  CONSTRAINT federation_term_federation_FK FOREIGN KEY (federation_id) REFERENCES federation(id) ON DELETE CASCADE,
                                  CONSTRAINT federation_term_position_FK FOREIGN KEY (position_id) REFERENCES position(id) ON DELETE CASCADE,
                                  CONSTRAINT federation_term_vote_FK FOREIGN KEY (vote_id) REFERENCES federation_vote(id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Table: membership_history
-- Description: Membership history of a member in a collectivity
-- ------------------------------------------------------------
CREATE TABLE membership_history (
                                     id VARCHAR(20) PRIMARY KEY,
                                     start_date DATE NOT NULL,
                                     end_date DATE,
                                     reason VARCHAR(30) NOT NULL CHECK (reason IN ('ADMISSION', 'TRANSFER', 'RESIGNATION')),
                                     member_id VARCHAR(20) NOT NULL,
                                     collectivity_id VARCHAR(20) NOT NULL,
                                     CONSTRAINT membership_history_member_FK FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
                                     CONSTRAINT membership_history_collectivity_FK FOREIGN KEY (collectivity_id) REFERENCES collectivity(id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Table: sponsorship
-- Description: Sponsorship relationship between two members
-- ------------------------------------------------------------
CREATE TABLE sponsorship (
                              id VARCHAR(20) PRIMARY KEY,
                              sponsorship_date DATE NOT NULL,
                              sponsor_member_id VARCHAR(20) NOT NULL,
                              sponsored_member_id VARCHAR(20) NOT NULL,
                              CONSTRAINT sponsorship_sponsor_FK FOREIGN KEY (sponsor_member_id) REFERENCES member(id) ON DELETE CASCADE,
                              CONSTRAINT sponsorship_sponsored_FK FOREIGN KEY (sponsored_member_id) REFERENCES member(id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Table: contribution
-- Description: Payment from a member to a collectivity
-- ------------------------------------------------------------
CREATE TABLE contribution (
                               id VARCHAR(20) PRIMARY KEY,
                               amount DECIMAL(12,2) NOT NULL CHECK (amount > 0),
                               collection_date DATE NOT NULL,
                               payment_method VARCHAR(20) NOT NULL CHECK (payment_method IN ('CASH', 'BANK_TRANSFER', 'MOBILE_BANKING')),
                               type VARCHAR(20) NOT NULL CHECK (type IN ('ADMISSION', 'MONTHLY', 'ANNUAL', 'ONE_TIME', 'WEEKLY', 'PUNCTUALLY')),
                               federation_percentage DECIMAL(5,2) DEFAULT 0 CHECK (federation_percentage >= 0 AND federation_percentage <= 100),
                               member_id VARCHAR(20) NOT NULL,
                               collectivity_id VARCHAR(20) NOT NULL,
                               CONSTRAINT contribution_member_FK FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
                               CONSTRAINT contribution_collectivity_FK FOREIGN KEY (collectivity_id) REFERENCES collectivity(id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Table: account (Root Entity)
-- Description: Financial account (cash, bank or mobile money)
-- Specialization: type = 'bank' --> account_extended, type = 'mobile_money' --> account_mobile
-- ------------------------------------------------------------
CREATE TABLE account (
                          id VARCHAR(20) PRIMARY KEY,
                          type VARCHAR(20) NOT NULL CHECK (type IN ('cash', 'bank', 'mobile_money')),
                          collectivity_id VARCHAR(20),
                          federation_id VARCHAR(20),
                          CHECK (
                              (collectivity_id IS NOT NULL AND federation_id IS NULL)
                                  OR
                              (collectivity_id IS NULL AND federation_id IS NOT NULL)
                              ),
                          CONSTRAINT account_collectivity_FK FOREIGN KEY (collectivity_id) REFERENCES collectivity(id) ON DELETE CASCADE,
                          CONSTRAINT account_federation_FK FOREIGN KEY (federation_id) REFERENCES federation(id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Table: account_extended (Subtype - Banking)
-- Description: Bank account with bank details
-- Specialization: Inherits from account (type = 'bank')
-- ------------------------------------------------------------
CREATE TABLE account_extended (
                                   account_id VARCHAR(20) PRIMARY KEY,
                                   holder_name VARCHAR(150) NOT NULL,
                                   bank_name VARCHAR(30) NOT NULL CHECK (bank_name IN ('BRED', 'MCB', 'BMOI', 'BOA', 'BGFI', 'AFG', 'ACCES_BANQUE', 'BAOBAB', 'SIPEM')),
                                   account_number VARCHAR(23) NOT NULL,
                                   rib_key VARCHAR(2) NOT NULL,
                                   CONSTRAINT account_extended_account_FK FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Table: account_mobile (Subtype - Mobile Money)
-- Description: Mobile money account
-- Specialization: Inherits from account (type = 'mobile_money')
-- ------------------------------------------------------------
CREATE TABLE account_mobile (
                                 account_id VARCHAR(20) PRIMARY KEY,
                                 holder_name VARCHAR(150) NOT NULL,
                                 service_name VARCHAR(20) NOT NULL CHECK (service_name IN ('ORANGE_MONEY', 'MVOLA', 'AIRTEL_MONEY')),
                                 phone_number VARCHAR(20) NOT NULL,
                                 CONSTRAINT account_mobile_account_FK FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Table: transaction
-- Description: Financial operation on an account
-- ------------------------------------------------------------
CREATE TABLE "transaction" (
                                id VARCHAR(20) PRIMARY KEY,
                                account_id VARCHAR(20) NOT NULL,
                                amount DECIMAL(12,2) NOT NULL,
                                transaction_date DATE NOT NULL,
                                description VARCHAR(255),
                                member_id VARCHAR(20),
                                CONSTRAINT transaction_account_FK FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE,
                                CONSTRAINT transaction_member_FK FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE SET NULL
);

-- ------------------------------------------------------------
-- Table: activity
-- Description: Activity (meeting, training) organized by an entity
-- ------------------------------------------------------------
CREATE TABLE activity (
                           id VARCHAR(20) PRIMARY KEY,
                           label VARCHAR(100) NOT NULL,
                           activity_type VARCHAR(20) NOT NULL CHECK (activity_type IN ('MEETING', 'TRAINING', 'OTHER')),
                           executive_date DATE,
                           recurrence_week_ordinal INT CHECK (recurrence_week_ordinal BETWEEN 1 AND 5),
                           recurrence_day_of_week VARCHAR(2) CHECK (recurrence_day_of_week IN ('MO', 'TU', 'WE', 'TH', 'FR', 'SA', 'SU')),
                           collectivity_id VARCHAR(20),
                           federation_id VARCHAR(20),
                           CHECK (
                               (collectivity_id IS NOT NULL AND federation_id IS NULL)
                                   OR
                               (collectivity_id IS NULL AND federation_id IS NOT NULL)
                           ),
                           CHECK (
                               (executive_date IS NOT NULL AND recurrence_week_ordinal IS NULL AND recurrence_day_of_week IS NULL)
                                   OR
                               (executive_date IS NULL AND recurrence_week_ordinal IS NOT NULL AND recurrence_day_of_week IS NOT NULL)
                           ),
                           CONSTRAINT activity_collectivity_FK FOREIGN KEY (collectivity_id) REFERENCES collectivity(id) ON DELETE CASCADE,
                           CONSTRAINT activity_federation_FK FOREIGN KEY (federation_id) REFERENCES federation(id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Table: activity_occupation
-- Description: Occupations concerned by an activity
-- ------------------------------------------------------------
CREATE TABLE activity_occupation (
                                     activity_id VARCHAR(20),
                                     occupation VARCHAR(20) NOT NULL CHECK (occupation IN ('JUNIOR', 'SENIOR', 'SECRETARY', 'TREASURER', 'VICE_PRESIDENT', 'PRESIDENT')),
                                     PRIMARY KEY (activity_id, occupation),
                                     CONSTRAINT activity_occupation_activity_FK FOREIGN KEY (activity_id) REFERENCES activity(id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Table: attendance
-- Description: Attendance record for an activity
-- ------------------------------------------------------------
CREATE TABLE attendance (
                             id VARCHAR(20) PRIMARY KEY,
                             status VARCHAR(10) NOT NULL CHECK (status IN ('ATTENDED', 'MISSING', 'UNDEFINED')),
                             is_external BOOLEAN DEFAULT FALSE NOT NULL,
                             member_id VARCHAR(20) NOT NULL,
                             activity_id VARCHAR(20) NOT NULL,
                             CONSTRAINT attendance_member_FK FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
                             CONSTRAINT attendance_activity_FK FOREIGN KEY (activity_id) REFERENCES activity(id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- INDEXES
-- ------------------------------------------------------------

CREATE INDEX idx_member_collectivity ON membership_history(member_id, collectivity_id);
CREATE INDEX idx_collectivity_federation ON collectivity(federation_id);
CREATE INDEX idx_contribution_member ON contribution(member_id);
CREATE INDEX idx_contribution_collectivity ON contribution(collectivity_id);
CREATE INDEX idx_transaction_account ON "transaction"(account_id);
CREATE INDEX idx_attendance_member ON attendance(member_id);
CREATE INDEX idx_attendance_activity ON attendance(activity_id);
CREATE INDEX idx_account_collectivity ON account(collectivity_id);
CREATE INDEX idx_account_federation ON account(federation_id);
CREATE INDEX idx_activity_collectivity ON activity(collectivity_id);
CREATE INDEX idx_activity_federation ON activity(federation_id);
CREATE INDEX idx_activity_executive_date ON activity(executive_date);
CREATE INDEX idx_collectivity_status ON collectivity(status);

-- ============================================================
-- END OF DDL
-- ============================================================