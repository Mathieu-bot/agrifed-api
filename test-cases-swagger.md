# Test Cases pour Swagger UI

Utilisation des valeurs explicites de test_data_v0.0.3.sql

---

## DONNÉES DE TEST PRÉ-EXISTANTES

### Members (IDs 1-12)
| ID  | Nom                | Prénom       | Occupation    |
|-----|-------------------|-------------|--------------|
| 1   | Rakoto            | Jean        | PRESIDENT    |
| 2   | Rasoa            | Marie       | SECRETARY   |
| 3   | Randria          | Pierre      | TREASURER   |
| 4   | Razafy           | Sophie      | VICE_PRESIDENT |
| 5   | Andriana         | Paul        | JUNIOR      |
| 6   | Ramanantsoa      | Julie       | JUNIOR      |
| 7   | Ratsimba         | Marc        | JUNIOR      |
| 8   | Rakotovao        | Aimé        | PRESIDENT   |
| 9   | Randriamanantena  | Lina        | SECRETARY   |
| 10  | Ratsioro         | Bruno       | TREASURER   |
| 11  | Andriamanitra    | Claire      | VICE_PRESIDENT |
| 12  | Ramanantoandro   | Eric        | JUNIOR      |

### Collectivités
| ID  | Nom       | Specialty | Ville       |
|-----|----------|-----------|-------------|
| 1   | FIJABE   | Élevage   | Antananarivo |
| 2   | SAKAMASY | Agriculture | Toamasina |

### Membership Fees
| ID  | Label               | Frequency | Amount |
|-----|--------------------|-----------|--------|
| 1   | Cotisation mensuelle | MONTHLY  | 5000   |
| 2   | Cotisation annuelle | ANNUALLY | 50000  |
| 3   | Cotisation hebdomadaire | WEEKLY   | 1000   |
| 4   | Cotisation mensuelle | MONTHLY  | 3000   |
| 5   | Cotisation annuelle | ANNUALLY | 30000  |

### Comptes
| ID  | Type         | Collectivité | Holder   |
|-----|--------------|--------------|----------|
| 1   | cash         | 1 (FIJABE)   | -        |
| 2   | bank         | 1 (FIJABE)  | FIJABE   | Bank: BOA
| 3   | mobile_money | 1 (FIJABE)  | FIJABE   | Service: Mvola
| 4   | cash         | 2 (SAKAMASY)| -        |
| 5   | bank         | 2 (SAKAMASY)| SAKAMASY | Bank: BRED
| 6   | mobile_money | 2 (SAKAMASY)| SAKAMASY | Service: Orange_Money

---

## 1. POST /collectivities

### ✅ Cas valide: Collectivité avec structure complète (201 Created)

```json
[
  {
    "location": "District d'Antananarivo",
    "federationApproval": true,
    "members": ["1", "2", "3", "4", "5"],
    "structure": {
      "president": "1",
      "vicePresident": "4",
      "treasurer": "3",
      "secretary": "2"
    }
  }
]
```

### ❌ Cas invalide: Sans fédération approval (400 Bad Request)

```json
[
  {
    "location": "District de Mahajanga",
    "federationApproval": false,
    "members": ["1", "2", "3", "4", "5"]
  }
]
```

### ❌ Cas invalide: Pas assez de membres seniors (400 Bad Request)

```json
[
  {
    "location": "District de Fianarantsoa",
    "federationApproval": true,
    "members": ["1", "2", "3", "4"]
  }
]
```

---

## 2. PUT /collectivities/{id}/informations

### ✅ Cas valide: Mise à jour nom et numéro (200 OK)

URL: `/collectivities/1/informations`

```json
{
  "name": "Fédération des Éleveurs de FIJABE",
  "number": "FED-ELEV-001"
}
```

### ❌ Cas invalide: Nom déjà utilisé (400 Bad Request)

URL: `/collectivities/2/informations`

```json
{
  "name": "SAKAMASY",
  "number": "NEW-NUMBER"
}
```

### ❌ Cas invalide: Numéro déjà utilisé (400 Bad Request)

URL: `/collectivities/1/informations`

```json
{
  "name": "Nouveau Nom",
  "number": "COLL-002"
}
```

---

## 3. GET /collectivities/{id}/membershipFees

### ✅ Cas valide: Récupérer membership fees (200 OK)

URL: `/collectivities/1/membershipFees`

Réponse attendue:
```json
[
  {"id": "1", "eligibleFrom": "2024-01-01", "frequency": "MONTHLY", "amount": 5000, "label": "Cotisation mensuelle", "status": "ACTIVE"},
  {"id": "2", "eligibleFrom": "2024-01-01", "frequency": "ANNUALLY", "amount": 50000, "label": "Cotisation annuelle", "status": "ACTIVE"},
  {"id": "3", "eligibleFrom": "2024-01-01", "frequency": "WEEKLY", "amount": 1000, "label": "Cotisation hebdomadaire", "status": "ACTIVE"}
]
```

---

## 4. POST /collectivities/{id}/membershipFees

### ✅ Cas valide: Création membership fee mensuel (200 OK)

```json
[
  {
    "eligibleFrom": "2026-05-01",
    "frequency": "MONTHLY",
    "amount": 7500,
    "label": "Nouvelle Cotisation Mensuelle"
  }
]
```

### ✅ Cas valide: Toutes les fréquences (200 OK)

```json
[
  {
    "eligibleFrom": "2026-05-01",
    "frequency": "WEEKLY",
    "amount": 1500,
    "label": "Hebdomadaire"
  },
  {
    "eligibleFrom": "2026-05-01",
    "frequency": "MONTHLY",
    "amount": 6000,
    "label": "Mensuelle"
  },
  {
    "eligibleFrom": "2026-05-01",
    "frequency": "ANNUALLY",
    "amount": 60000,
    "label": "Annuelle"
  },
  {
    "eligibleFrom": "2026-05-01",
    "frequency": "PUNCTUALLY",
    "amount": 10000,
    "label": "Ponctuelle"
  }
]
```

### ❌ Cas invalide: Fréquence invalide (400 Bad Request)

```json
[
  {
    "eligibleFrom": "2026-05-01",
    "frequency": "DAILY",
    "amount": 1000,
    "label": "Invalide"
  }
]
```

### ❌ Cas invalide: Montant négatif (400 Bad Request)

```json
[
  {
    "eligibleFrom": "2026-05-01",
    "frequency": "MONTHLY",
    "amount": -100,
    "label": "Montant négatif"
  }
]
```

### ❌ Cas invalide: Amount = 0 (400 Bad Request)

```json
[
  {
    "eligibleFrom": "2026-05-01",
    "frequency": "MONTHLY",
    "amount": 0,
    "label": "Montant nul"
  }
]
```

---

## 5. GET /collectivities/{id}/transactions

### ✅ Cas valide: Transactions exist (200 OK)

URL: `/collectivities/1/transactions?from=2024-01-01&to=2026-12-31`

### ✅ Cas valide: Pas de transactions (200 OK - empty array)

URL: `/collectivities/1/transactions?from=2025-01-01&to=2025-12-31`

### ❌ Cas invalide: Paramètre `from` manquant (400 Bad Request)

URL: `/collectivities/1/transactions?to=2026-12-31`

### ❌ Cas invalide: Paramètre `to` manquant (400 Bad Request)

URL: `/collectivities/1/transactions?from=2026-01-01`

---

## 6. POST /members

### ✅ Cas valide: Nouveau membre avec JUNIOR (201 Created)

Selon le cahier de charges: Le candidat doit être parrainé par au moins 2 membres.
- Les referees sont maintenant des strings: ["1", "2"]

```json
[
  {
    "firstName": "Nouveau",
    "lastName": "Membre",
    "birthDate": "2000-01-15",
    "gender": "MALE",
    "address": "Antananarivo",
    "profession": "Agriculteur",
    "phoneNumber": "0349999999",
    "email": "nouveau.membre@email.com",
    "occupation": "JUNIOR",
    "collectivityIdentifier": "1",
    "referees": ["1", "2"],
    "registrationFeePaid": true,
    "membershipDuesPaid": true
  }
]
```

### ✅ Cas valide: Nouveau membre avec SENIOR - 3 parrains (201 Created)

3 parrains: 2 de la même collectivité + 1 d'ailleurs (autorisé selon cahier de charges)

```json
[
  {
    "firstName": "Membre",
    "lastName": "Senior",
    "birthDate": "1985-06-20",
    "gender": "MALE",
    "address": "Antananarivo",
    "profession": "Agriculteur",
    "phoneNumber": "0348888888",
    "email": "senior.membre@email.com",
    "occupation": "SENIOR",
    "collectivityIdentifier": "1",
    "referees": ["1", "5", "8"],
    "registrationFeePaid": true,
    "membershipDuesPaid": true
  }
]
```

### ✅ Cas valide: Gender FEMALE (201 Created)

```json
[
  {
    "firstName": "Nouvelle",
    "lastName": "Membre",
    "birthDate": "1998-03-25",
    "gender": "FEMALE",
    "address": "Toamasina",
    "profession": "Agricultrice",
    "phoneNumber": "0337777777",
    "email": "nouvelle.membre@email.com",
    "occupation": "JUNIOR",
    "collectivityIdentifier": "2",
    "referees": ["8", "9"],
    "registrationFeePaid": true,
    "membershipDuesPaid": true
  }
]
```

### ❌ Cas invalide: Un seul parrain (400 Bad Request)

```json
[
  {
    "firstName": "Test1",
    "lastName": "Membre",
    "birthDate": "1995-01-01",
    "gender": "MALE",
    "address": "Test",
    "profession": "Test",
    "phoneNumber": "0340000001",
    "email": "test1@email.com",
    "occupation": "JUNIOR",
    "collectivityIdentifier": "1",
    "referees": ["1"],
    "registrationFeePaid": true,
    "membershipDuesPaid": true
  }
]
```

### ❌ Cas invalide: Registration fee non payé (400 Bad Request)

```json
[
  {
    "firstName": "Test1",
    "lastName": "Membre",
    "birthDate": "1995-01-01",
    "gender": "MALE",
    "address": "Test",
    "profession": "Test",
    "phoneNumber": "0340000001",
    "email": "test1@email.com",
    "occupation": "JUNIOR",
    "collectivityIdentifier": "1",
    "referees": ["1", "2"],
    "registrationFeePaid": false,
    "membershipDuesPaid": true
  }
]
```

### ❌ Cas invalide: Membership dues non payé (400 Bad Request)

```json
[
  {
    "firstName": "Test2",
    "lastName": "Membre",
    "birthDate": "1995-01-01",
    "gender": "MALE",
    "address": "Test",
    "profession": "Test",
    "phoneNumber": "0340000002",
    "email": "test2@email.com",
    "occupation": "JUNIOR",
    "collectivityIdentifier": "1",
    "referees": ["1", "2"],
    "registrationFeePaid": true,
    "membershipDuesPaid": false
  }
]
```

### ❌ Cas invalide: Referees inexistants (404 Not Found)

```json
[
  {
    "firstName": "Test3",
    "lastName": "Membre",
    "birthDate": "1995-01-01",
    "gender": "MALE",
    "address": "Test",
    "profession": "Test",
    "phoneNumber": "0340000003",
    "email": "test3@email.com",
    "occupation": "JUNIOR",
    "collectivityIdentifier": "1",
    "referees": ["999", "998"],
    "registrationFeePaid": true,
    "membershipDuesPaid": true
  }
]
```

### ❌ Cas invalide: Moitié des parrains pas de la collectivite cible (400 Bad Request)

Exemple: 2 parrains de la collectivité 2 pour postuler à la collectivité 1 (refusé selon cahier de charges)

```json
[
  {
    "firstName": "Test4",
    "lastName": "Membre",
    "birthDate": "1995-01-01",
    "gender": "MALE",
    "address": "Test",
    "profession": "Test",
    "phoneNumber": "0340000004",
    "email": "test4@email.com",
    "occupation": "JUNIOR",
    "collectivityIdentifier": "1",
    "referees": ["8", "9"],
    "registrationFeePaid": true,
    "membershipDuesPaid": true
  }
]
```

---

## 7. POST /members/{id}/payments

IDs des comptes disponibles:
- `1`: Cash FIJABE
- `2`: Bank FIJABE (BOA)
- `3`: Mobile Money FIJABE (Mvola)
- `4`: Cash SAKAMASY
- `5`: Bank SAKAMASY (BRED)
- `6`: Mobile Money SAKAMASY (Orange Money)

IDs des membership fees:
- `1`: monthly 5000 (FIJABE)
- `2`: annually 50000 (FIJABE)
- `3`: weekly 1000 (FIJABE)
- `4`: monthly 3000 (SAKAMASY)
- `5`: annually 30000 (SAKAMASY)

### ✅ Cas valide: Payment en espèces (CASH) sur compte cash (201 Created)

```json
[
  {
    "amount": 5000,
    "membershipFeeIdentifier": "1",
    "accountCreditedIdentifier": "1",
    "paymentMode": "CASH"
  }
]
```

### ✅ Cas valide: Payment par mobile banking (MOBILE_BANKING) sur compte mobile (201 Created)

```json
[
  {
    "amount": 5000,
    "membershipFeeIdentifier": "1",
    "accountCreditedIdentifier": "3",
    "paymentMode": "MOBILE_BANKING"
  }
]
```

### ✅ Cas valide: Payment par virement bancaire (BANK_TRANSFER) sur compte banque (201 Created)

```json
[
  {
    "amount": 50000,
    "membershipFeeIdentifier": "2",
    "accountCreditedIdentifier": "2",
    "paymentMode": "BANK_TRANSFER"
  }
]
```

### ❌ Cas invalide: Account non trouvé

```json
[
  {
    "amount": 5000,
    "membershipFeeIdentifier": "1",
    "accountCreditedIdentifier": "999",
    "paymentMode": "CASH"
  }
]
```

---

## Énumérations

### Gender
- `MALE`
- `FEMALE`

### MemberOccupation
- `JUNIOR`
- `SENIOR`
- `SECRETARY`
- `TREASURER`
- `VICE_PRESIDENT`
- `PRESIDENT`

### Frequency
- `WEEKLY`
- `MONTHLY`
- `ANNUALLY`
- `PUNCTUALLY`

### ActivityStatus
- `ACTIVE`
- `INACTIVE`

### PaymentMode
- `CASH`
- `MOBILE_BANKING`
- `BANK_TRANSFER`

### Bank (pour BankAccount)
- `BRED`
- `MCB`
- `BMOI`
- `BOA`
- `BGFI`
- `AFG`
- `ACCES_BANQUE`
- `BAOBAB`
- `SIPEM`

### MobileBankingService (pour MobileBankingAccount)
- `AIRTEL_MONEY`
- `MVOLA`
- `ORANGE_MONEY`