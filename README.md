# Multi-Level Parking Management System

A standalone GUI application designed for university parking lot management. This system handles multi-level spot allocation, vehicle-specific pricing, and automated fine enforcement.

## 🏢 System Specifications
- **Capacity:** 5 Floors.
- **Spot Types:** Compact (RM 2/hr), Regular (RM 5/hr), Handicapped (RM 2/hr), Reserved (RM 10/hr).
- **Rounding:** Ceiling rounding (e.g., 1.1 hours = 2 hours). 

---

## 🛠️ Panel Breakdown & Task List

### 1. Entry/Exit Panel (Operator Interface)
*Handles the daily flow of vehicles and payments.*

- [ ] **Vehicle Entry Module**
    - Input: License Plate and Vehicle Type selection.
    - Logic: Filter available spots based on vehicle compatibility:
        - *Motorcycle:* Compact only.
        - *Car:* Compact or Regular.
        - *SUV/Truck:* Regular only.
        - *Handicapped:* Any spot (RM 2/hr applies if card is present).
    - Action: Mark spot as `Occupied` and record `Entry Time`.
    - Output: Display ticket `T-PLATE-TIMESTAMP`.

- [ ] **Vehicle Exit Module**
    - Input: License Plate search.
    - Logic: 
        - Find entry time and calculate total duration.
        - Calculate fee based on spot hourly rate.
        - Retrieve any existing unpaid fines linked to the plate.
    - Action: Mark spot as `Available` upon payment.

- [ ] **Payment Processing**
    - Support: Cash and Card payments.
    - Output: Generate receipt including breakdown of hours, rates, fines, and total.

### 2. Admin Panel (Management Interface)
*Handles system configuration and facility oversight.*

- [ ] **Live Monitoring**
    - Visual floor map showing all 5 levels.  ✅ 
    - Real-time occupancy rate (%) and total revenue counter. ✅ 
    - View list of all currently parked vehicles. ✅ 

- [ ] **Fine Configuration** 
    - Option to select the active fine scheme:
        - **Option A:** Fixed Fine (RM 50). ✅ 
        - **Option B:** Progressive (RM 50 -> RM 100 -> RM 150 -> RM 200). 
        - **Option C:** Hourly (RM 20/hr overstay). 
    - *Note: Applied to new entries only.* 

- [ ] **Fine Management** 
    - View/Edit outstanding fines linked to specific license plates. 

### 3. Reporting Panel (Analytics Interface)
*Generates data summaries for administrative review.*

- [ ] **Occupancy Report:** Breakdown of space utilization by floor and spot type.
- [ ] **Revenue Report:** Detailed log of all collected fees and fines.
- [ ] **Fine Report:** List of all outstanding fines and debt by plate number.
- [ ] **Inventory:** List of all vehicles currently on-site.

---

## ⚖️ Business Rules & Rates

| Vehicle Type | Valid Spots | Default Rate | Fine Conditions |
| :--- | :--- | :--- | :--- |
| **Motorcycle** | Compact | RM 2/hr | Over 24hrs / Wrong Spot |
| **Car** | Compact, Regular | RM 5/hr | Over 24hrs / Wrong Spot |
| **SUV / Truck** | Regular | RM 5/hr | Over 24hrs / Wrong Spot |
| **Handicapped** | Any | RM 2/hr* | Over 24hrs |
| **VIP** | Reserved | RM 10/hr | Over 24hrs |

*\*Handicapped rate requires valid card holder verification.*

---

## 🚀 Future Enhancements
- [ ] Integration with automated license plate recognition (ALPR).
- [ ] Pre-booking system for Reserved/VIP spots via web portal.
- [ ] PDF export for daily revenue reports.