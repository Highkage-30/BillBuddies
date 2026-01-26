# 💸 BillBuddies

**BillBuddies** is a smart expense settlement system designed to efficiently manage shared expenses among groups using **netting** and **novation**, powered by a **Central Counter Party (CCP)** called **BillBuddy**.

Unlike traditional expense split apps, BillBuddies minimizes unnecessary peer-to-peer settlements by routing obligations through a central entity, resulting in cleaner balances, fewer transactions, and transparent statements.

---

## 🎯 Project Objective

The goal of BillBuddies is to:

- Simplify group expense management  
- Reduce the number of settlement transactions  
- Provide clear, member-wise and group-wise statements  
- Apply real-world financial concepts (netting & novation) in a practical system  

---

## 🧠 Core Financial Concepts Used

### 🔁 Novation
Novation is a legal and financial process where one party in a contract is replaced by a third party, extinguishing the original contract and creating a new one with the consent of all involved.

**In BillBuddies:**  
All inter-member debts are novated to a **Central Counter Party (BillBuddy)**.  
Members no longer owe each other directly — they owe or receive from BillBuddy.

Before Novation:
<img width="541" height="81" alt="Untitled Diagram drawio" src="https://github.com/user-attachments/assets/973508ed-4680-4460-a090-dae2b84dcd48" />
After Novation: 
<img width="801" height="81" alt="Untitled Diagram drawio (1)" src="https://github.com/user-attachments/assets/953e6030-e84d-4eb1-a022-7bc1e00922ab" />

---

### ➕ Netting
Netting is the process of offsetting multiple financial obligations between parties to determine a single net amount payable or receivable.

**In BillBuddies:**  
Multiple expenses and splits are netted to calculate a **single net balance per member**, reducing transaction volume and complexity.

---

## 🏗️ System Overview

### Central Counter Party (CCP)
- **BillBuddy** acts as a common settlement account
- All expenses are ultimately settled against BillBuddy
- Ensures clean ledgers and simplified settlements

---

## ✨ Key Features

- 👥 Create and manage **expense groups**
- ➕ Add and manage **group members**
- 💰 Record expenses (paid by any member or CCP)
- 🔄 Automatic **novation of expenses** to BillBuddy
- 📊 **Netting** of balances to compute final dues
- 🧾 Generate **mini statements**:
  - Member-wise
  - Group-wise
- ✅ Clear settlement status for each member

---

## 🧩 Functional Workflow

1. Create a group
2. Add members to the group
3. Record expenses (who paid, amount, group)
4. Expenses are novated to **BillBuddy (CCP)**
5. Netting logic calculates final balances
6. Members view statements and settle dues

---

## 🗃️ High-Level Data Model

- **Member**
  - memberId
  - memberName

- **Group**
  - groupId
  - groupName
  - description

- **Original Expense**
  - paidBy
  - paidFor
  - amount

- **Split Expense (After Novation)**
  - member ↔ BillBuddy
  - net amount

- **Statement**
  - member
  - group
  - net payable / receivable

---

## 🛠️ Tech Stack (Planned / In Progress)

- **Frontend:** React  
- **Backend:** Spring Boot  
- **Database:** MySQL / PostgreSQL  
- **Authentication:** JWT  
- **Architecture:** REST APIs, role-based access  

---

## 🚀 Future Enhancements

- Settlement history & audit trail
- Export statements (PDF / Excel)
- Notifications & reminders
- Multi-currency support
- Advanced analytics on spending patterns

---

## 📌 Why BillBuddies?

BillBuddies goes beyond simple expense splitting by applying **real financial clearing concepts** used in banking and capital markets, making it both a **practical utility** and a **strong academic / portfolio project**.

---

## 📄 License

This project is for educational and personal use.  
Feel free to fork, experiment, and enhance.

---
