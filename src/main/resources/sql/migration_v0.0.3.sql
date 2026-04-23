CREATE TABLE membership_fee (
                                id SERIAL PRIMARY KEY,
                                eligible_from DATE NOT NULL,
                                frequency VARCHAR(20) NOT NULL CHECK (frequency IN ('WEEKLY','MONTHLY','ANNUALLY','PUNCTUALLY')),
                                amount DECIMAL(12,2) NOT NULL CHECK (amount > 0),
                                label VARCHAR(150),
                                status VARCHAR(10) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','INACTIVE')),
                                collectivity_id INTEGER NOT NULL,
                                CONSTRAINT membership_fee_collectivity_FK FOREIGN KEY (collectivity_id) REFERENCES collectivity(id) ON DELETE CASCADE
);

CREATE TABLE member_payment (
                                id SERIAL PRIMARY KEY,
                                amount DECIMAL(12,2) NOT NULL CHECK (amount > 0),
                                payment_mode VARCHAR(20) NOT NULL CHECK (payment_mode IN ('CASH','MOBILE_BANKING','BANK_TRANSFER')),
                                member_id INTEGER NOT NULL,
                                membership_fee_id INTEGER NOT NULL,
                                account_credited_id INTEGER NOT NULL,
                                creation_date DATE NOT NULL DEFAULT CURRENT_DATE,
                                CONSTRAINT member_payment_member_FK FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
                                CONSTRAINT member_payment_fee_FK FOREIGN KEY (membership_fee_id) REFERENCES membership_fee(id) ON DELETE CASCADE,
                                CONSTRAINT member_payment_account_FK FOREIGN KEY (account_credited_id) REFERENCES account(id) ON DELETE CASCADE
);