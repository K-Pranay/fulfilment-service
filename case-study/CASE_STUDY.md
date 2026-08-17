# Case Study Scenarios to discuss

## Scenario 1: Cost Allocation and Tracking
**Situation**: The company needs to track and allocate costs accurately across different Warehouses and Stores. The costs include labor, inventory, transportation, and overhead expenses.

**Task**: Discuss the challenges in accurately tracking and allocating costs in a fulfillment environment. Think about what are important considerations for this, what are previous experiences that you have you could related to this problem and elaborate some questions and considerations

**Questions you may have and considerations:**
```txt
1. Key Challenges:
   - Shared Overhead Allocation: Fixed costs (facility leases, utilities, warehouse management software licensing) must be fairly distributed across multiple stores and product lines. Direct allocation based solely on volume can distort profitability for high-volume, low-margin products.
   - Dynamic Labor Tracking: Warehouse personnel switch between receiving, pick/pack, and returns handling. Without activity-based costing (ABC), labor costs are often inappropriately averaged across all SKUs.
   - Last-Mile Transportation Volatility: Dynamic carrier rates, fuel surcharges, and split shipments make per-order freight allocation complex.

2. Considerations & Architectural Approach:
   - Activity-Based Costing (ABC) Model: Event-driven tracking via microservice events (e.g., `OrderPicked`, `ItemRestocked`, `ShipmentDispatched`) to capture exact resource consumption per SKU, Store, and Warehouse.
   - Cost Dimensions: Tag all ledger entries with multi-dimensional keys: `location_id`, `warehouse_bu_code`, `store_id`, `sku_category`, and `cost_center`.

3. Strategic Questions to Stakeholders:
   - What granularity of cost reporting is required by finance (per item, per order, per store, per day)?
   - How should multi-fulfillment orders (one order fulfilled by two different warehouses) split freight costs between store P&L statements?
```

## Scenario 2: Cost Optimization Strategies
**Situation**: The company wants to identify and implement cost optimization strategies for its fulfillment operations. The goal is to reduce overall costs without compromising service quality.

**Task**: Discuss potential cost optimization strategies for fulfillment operations and expected outcomes from that. How would you identify, prioritize and implement these strategies?

**Questions you may have and considerations:**
```txt
1. Cost Optimization Strategies:
   - Optimal Warehouse Colocation & Nearest-Neighbor Routing: Assigning store orders to the geographic warehouse with the lowest transit zone cost and highest stock availability, minimizing split shipments.
   - Stock Density & Slotting Optimization: Grouping high-velocity SKUs near packing stations to reduce picker travel time (slashing internal labor costs by up to 25%).
   - Dynamic Order Batching & Consolidated Freight: Consolidating store replenishment shipments into scheduled truckloads rather than ad-hoc small parcel shipments.

2. Identification & Prioritization Framework (Impact vs. Effort Matrix):
   - High Impact / Low Effort (Priority 1): Intelligent order routing algorithm optimization (software update, no physical layout changes required).
   - High Impact / Medium Effort (Priority 2): Inventory rebalancing based on historical store demand patterns to reduce cross-region fulfillment.
   - Medium Impact / High Effort (Priority 3): Warehouse layout restructuring and automated storage equipment installation.

3. Strategic Questions to Stakeholders:
   - What are the SLA boundaries for store replenishment (e.g., standard 24h window vs urgent 6h)?
   - Is stock transfer between warehouses permitted to rebalance localized regional demand?
```

## Scenario 3: Integration with Financial Systems
**Situation**: The Cost Control Tool needs to integrate with existing financial systems to ensure accurate and timely cost data. The integration should support real-time data synchronization and reporting.

**Task**: Discuss the importance of integrating the Cost Control Tool with financial systems. What benefits the company would have from that and how would you ensure seamless integration and data synchronization?

**Questions you may have and considerations:**
```txt
1. Importance & Business Value:
   - Real-Time Financial Visibility: Eliminates month-end manual reconciliation surprises by providing immediate visibility into fulfillment expenditure against budget.
   - Accurate Unit Economics: Enables precise margin calculation down to individual product items and retail store channels.
   - Auditability & Compliance: Ensures complete audit trails between operational actions (warehouse replacement, stock movements) and general ledger accounts.

2. Integration Architecture & Reliability Strategy:
   - Event-Driven Asynchronous Integration (Kafka / AWS EventBridge): Decouples core fulfillment operations from financial ERP systems (SAP, Oracle, NetSuite) so ERP downtime does not block warehouse operations.
   - Transactional Outbox Pattern: Guarantees at-least-once delivery of financial cost events by committing ledger events to a database outbox table within the local DB transaction.
   - Idempotency & Reconciliation Jobs: Every financial transaction payload includes an `idempotencyKey` (UUID / transaction hash). Automated nightly reconciliation jobs verify that all operational events match financial posting records.

3. Strategic Questions to Stakeholders:
   - What is the legacy ERP system protocol (REST API, SOAP, sFTP CSV batches, Webhooks)?
   - How should connection outages or failed financial ledger postings be handled (automatic retries, dead-letter queues, manual review dashboard)?
```

## Scenario 4: Budgeting and Forecasting
**Situation**: The company needs to develop budgeting and forecasting capabilities for its fulfillment operations. The goal is to predict future costs and allocate resources effectively.

**Task**: Discuss the importance of budgeting and forecasting in fulfillment operations and what would you take into account designing a system to support accurate budgeting and forecasting?

**Questions you may have and considerations:**
```txt
1. Importance in Fulfillment Operations:
   - Capacity & Workforce Planning: Forecasts dictate warehouse seasonal staffing, shift scheduling, and temporary space leases prior to peak retail seasons (e.g., Q4 Black Friday).
   - Capital Expense (CapEx) Allocation: Helps leadership evaluate when a warehouse location is approaching max capacity, signaling the need to build or replace a warehouse.

2. Key System Design Considerations:
   - Historical & Seasonal Baseline Modeling: Factor in seasonal demand peaks, historical growth rates per store, and location max capacity constraints.
   - Flexible Parameter Simulation ("What-If" Analysis): Allow business planners to simulate cost outcomes based on variables like fuel price hikes, labor wage adjustments, or opening a new store location.
   - Rolling Forecasts & Variance Tracking: Continuously compare actual expenditures against budget forecasts to dynamically adjust forward projections monthly.

3. Strategic Questions to Stakeholders:
   - What external variables (inflation indices, carrier rate cards, promotional calendars) should feed into the forecasting model?
   - What error tolerance (e.g., MAPE < 5%) is acceptable for quarterly operational budget predictions?
```

## Scenario 5: Cost Control in Warehouse Replacement
**Situation**: The company is planning to replace an existing Warehouse with a new one. The new Warehouse will reuse the Business Unit Code of the old Warehouse. The old Warehouse will be archived, but its cost history must be preserved.

**Task**: Discuss the cost control aspects of replacing a Warehouse. Why is it important to preserve cost history and how this relates to keeping the new Warehouse operation within budget?

**Questions you may have and considerations:**
```txt
1. Importance of Preserving Cost History:
   - Historical Cost Benchmarking: Preserving cost history under the shared `businessUnitCode` allows finance to evaluate operational efficiency improvements (e.g., cost per unit fulfilled before vs after replacement).
   - Depreciation & Fixed Asset Accounting: Old facility asset write-downs and capital migration must be tracked separately from operational expenditure to avoid distorting operational performance metrics.
   - Regulatory & Audit Compliance: Financial compliance requires retaining 7+ years of transaction logs for audited business units.

2. Cost Control & Budget Management:
   - Transition & Overlap Budgeting: During replacement, both old and new facilities may run concurrently for stock transfer. Preserving historical baselines prevents double-counting inventory and ensures transition costs are budgeted as one-off CapEx/OpEx investments.
   - Capacity & Yield Tracking: Verifying that the new warehouse's increased capacity delivers expected lower per-unit cost metrics compared to the archived facility.

3. Strategic Questions to Stakeholders:
   - How should historical reporting distinguish between pre-replacement and post-replacement financial periods for the same Business Unit Code?
   - What is the policy for transferring remaining active stock value from the archived warehouse record to the replacement record?
```
