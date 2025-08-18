# Validation Workflow Diagram

This diagram shows the complete validation workflow for AP Clerks processing documents in the Apex IDP platform.

## Validation Queue Management

```mermaid
graph TD
    A[AP Clerk Login] --> B[Validation Dashboard]
    B --> C[View Document Queue]

    C --> D{Documents Available?}
    D -->|No| E[Show Empty Queue Message]
    D -->|Yes| F[Display Document List]

    E --> G[Wait for New Documents]
    G --> H[WebSocket Notification]
    H --> C

    F --> I[Sort/Filter Options]
    I --> J[Select Document]
    J --> K[Open Document Viewer]

    K --> L[Split Screen Layout]
    L --> M[Document Image/PDF]
    L --> N[Extracted Data Fields]

    M --> O[Zoom/Pan Controls]
    M --> P[Annotation Tools]
    N --> Q[Field Validation]

    Q --> R{Field Accuracy Check}
    R -->|Correct| S[Mark Field as Validated]
    R -->|Incorrect| T[Edit Field Value]
    R -->|Uncertain| U[Add Comment/Note]

    T --> V[Update Confidence Score]
    U --> W[Flag for Supervisor Review]
    S --> X[Next Field]

    X --> Y{All Fields Validated?}
    Y -->|No| Q
    Y -->|Yes| Z[Document Actions]

    Z --> AA{Action Choice}
    AA -->|Approve| BB[Approve Document]
    AA -->|Reject| CC[Reject Document]
    AA -->|Escalate| DD[Send to Supervisor]
    AA -->|Save Draft| EE[Save Progress]

    BB --> FF[Add to Approved Queue]
    CC --> GG[Rejection Workflow]
    DD --> HH[Supervisor Queue]
    EE --> II[Return to Queue]

    FF --> JJ[ERP Integration]
    GG --> KK[Notify Vendor]
    HH --> LL[Supervisor Review]
    II --> C

    classDef clerk fill:#4A90E2,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef validation fill:#7ED321,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef decision fill:#FF9500,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef action fill:#9013FE,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef endpoint fill:#50E3C2,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef workflow fill:#E17055,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF

    class A,B,C,F,I,J,K clerk
    class L,M,N,O,P,Q,S,T,U,V,W,X validation
    class D,R,Y,AA decision
    class BB,CC,DD,EE action
    class FF,JJ endpoint
    class GG,HH,KK,LL workflow
```

## Field-by-Field Validation Process

```mermaid
graph TD
    A[Start Field Validation] --> B[Load Extracted Data]
    B --> C[Display Field with Confidence]

    C --> D{Confidence Level}
    D -->|High >90%| E[Auto-highlight Green]
    D -->|Medium 70-90%| F[Highlight Yellow]
    D -->|Low <70%| G[Highlight Red]

    E --> H[Clerk Quick Review]
    F --> I[Clerk Careful Review]
    G --> J[Clerk Manual Verification]

    H --> K{Quick Validation}
    K -->|Accept| L[Mark as Validated]
    K -->|Reject| M[Edit Field]

    I --> N{Careful Validation}
    N -->|Accept| L
    N -->|Modify| M
    N -->|Uncertain| O[Add Note]

    J --> P[Manual Data Entry]
    P --> Q[Cross-reference Original]
    Q --> R[Enter Correct Value]

    M --> S[Update Field Value]
    O --> T[Flag for Review]
    R --> U[Update with Confidence 100%]

    S --> V[Recalculate Confidence]
    T --> W[Add to Review List]
    U --> X[Mark as Manually Verified]
    L --> Y[Save Field State]

    V --> Y
    W --> Y
    X --> Y

    Y --> Z{More Fields?}
    Z -->|Yes| AA[Next Field]
    Z -->|No| BB[Document Complete]

    AA --> B
    BB --> CC[Calculate Document Score]
    CC --> DD[Final Validation Decision]

    classDef confidence fill:#00B894,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef review fill:#F5A623,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef manual fill:#BD10E0,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef decision fill:#FF9500,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef completion fill:#4A90E2,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF

    class C,D,E,F,G,H,I,J confidence
    class K,N,S,V,W,Y review
    class P,Q,R,U manual
    class Z,AA decision
    class BB,CC,DD completion
```

## Document Annotation System

```mermaid
graph TD
    A[Document Viewer Loaded] --> B[Annotation Toolbar]

    B --> C{Annotation Type}
    C -->|Highlight| D[Text Highlighting Tool]
    C -->|Comment| E[Comment Bubble Tool]
    C -->|Arrow| F[Arrow Pointer Tool]
    C -->|Rectangle| G[Rectangle Selection Tool]
    C -->|Free Draw| H[Free Drawing Tool]

    D --> I[Select Text Region]
    E --> J[Click to Add Comment]
    F --> K[Draw Arrow Direction]
    G --> L[Draw Rectangle Area]
    H --> M[Free Hand Drawing]

    I --> N[Choose Highlight Color]
    J --> O[Enter Comment Text]
    K --> P[Add Arrow Label]
    L --> Q[Add Rectangle Label]
    M --> R[Save Drawing Path]

    N --> S[Yellow - Attention Needed]
    N --> T[Red - Error Found]
    N --> U[Green - Validated Correct]
    N --> V[Blue - Information]

    O --> W[Save Comment with Position]
    P --> X[Save Arrow with Text]
    Q --> Y[Save Rectangle with Label]
    R --> Z[Save Drawing Coordinates]

    S --> AA[Apply Highlight]
    T --> AA
    U --> AA
    V --> AA

    AA --> BB[Update Annotation Layer]
    W --> BB
    X --> BB
    Y --> BB
    Z --> BB

    BB --> CC[Save to Document Record]
    CC --> DD[Sync with Other Users]
    DD --> EE[Show in Annotation List]

    EE --> FF{More Annotations?}
    FF -->|Yes| C
    FF -->|No| GG[Complete Annotation]

    GG --> HH[Export Annotated Document]
    HH --> II[Include in Validation Report]

    classDef tool fill:#6C5CE7,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef action fill:#F5A623,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef color fill:#E17055,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef save fill:#50E3C2,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef completion fill:#74B9FF,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF

    class B,C,D,E,F,G,H tool
    class I,J,K,L,M,N,O,P,Q,R action
    class S,T,U,V color
    class W,X,Y,Z,AA,BB,CC save
    class DD,EE,GG,HH,II completion
```

## Batch Validation Workflow

```mermaid
graph TD
    A[Batch Arrives in Queue] --> B[Batch Summary View]
    B --> C[Show Batch Statistics]

    C --> D[Total Documents: X]
    C --> E[High Confidence: Y]
    C --> F[Needs Review: Z]
    C --> G[Processing Errors: W]

    D --> H{Batch Action Choice}
    H -->|Review All| I[Individual Document Review]
    H -->|Approve High Confidence| J[Bulk Approve Workflow]
    H -->|Review Low Confidence Only| K[Filtered Review Workflow]
    H -->|Reject Entire Batch| L[Batch Rejection Workflow]

    I --> M[Document-by-Document Review]
    M --> N[Standard Validation Process]
    N --> O{Document Complete?}
    O -->|Yes| P[Next Document in Batch]
    O -->|No| N
    P --> Q{More Documents?}
    Q -->|Yes| M
    Q -->|No| R[Batch Validation Complete]

    J --> S[Select High Confidence Items]
    S --> T[Bulk Approval Confirmation]
    T --> U[Approve Selected Documents]
    U --> V[Review Remaining Documents]
    V --> W{All Reviewed?}
    W -->|No| V
    W -->|Yes| R

    K --> X[Filter by Confidence <70%]
    X --> Y[Review Only Low Confidence]
    Y --> Z[Individual Validation]
    Z --> AA{Low Confidence Items Done?}
    AA -->|No| Z
    AA -->|Yes| BB[Auto-approve High Confidence]
    BB --> R

    L --> CC[Batch Rejection Reason]
    CC --> DD[Notify Vendor/Submitter]
    DD --> EE[Return Entire Batch]
    EE --> FF[Update Batch Status]

    R --> GG[Calculate Batch Metrics]
    GG --> HH[Approved: A documents]
    GG --> II[Rejected: B documents]
    GG --> JJ[Total Processing Time: T]

    HH --> KK[Send to ERP Integration]
    II --> LL[Generate Rejection Report]
    JJ --> MM[Update Performance Metrics]

    KK --> NN[Batch Processing Complete]
    LL --> NN
    MM --> NN
    FF --> NN

    classDef batch fill:#6C5CE7,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef statistics fill:#FDCB6E,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef workflow fill:#9013FE,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef completion fill:#50E3C2,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef rejection fill:#D0021B,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF

    class A,B,C,D,E,F,G batch
    class H,I,J,K,L,S,T,X,Y statistics
    class M,N,V,Z,CC,DD workflow
    class R,GG,KK,NN completion
    class L,CC,DD,EE,FF,LL rejection
```

## Real-time Collaboration Features

```mermaid
sequenceDiagram
    participant C1 as Clerk 1
    participant C2 as Clerk 2
    participant W as WebSocket
    participant B as Backend
    participant D as Database

    Note over C1,D: Document Lock System
    C1->>B: Open Document for Validation
    B->>D: Check Document Lock Status
    D->>B: Document Available
    B->>D: Lock Document for C1
    B->>C1: Document Opened Successfully
    B->>W: Broadcast Document Locked
    W->>C2: Notify Document Unavailable

    Note over C1,D: Real-time Updates
    C1->>B: Update Field Value
    B->>D: Save Field Update
    B->>W: Broadcast Field Updated
    W->>C2: Show Field Changed (if viewing)

    Note over C1,D: Validation Progress
    C1->>B: Mark Field as Validated
    B->>D: Update Validation Status
    B->>W: Broadcast Progress Update
    W->>C2: Update Queue Progress Indicators

    Note over C1,D: Document Completion
    C1->>B: Complete Document Validation
    B->>D: Save Final Validation
    B->>D: Release Document Lock
    B->>W: Broadcast Document Available
    W->>C2: Notify Document Available in Queue

    Note over C1,D: Supervisor Escalation
    C1->>B: Escalate to Supervisor
    B->>D: Update Document Status
    B->>W: Notify Supervisor Queue
    W->>Supervisor: New Escalation Alert
```

---

## Dark Mode Color Legend

The following color scheme has been optimized for dark backgrounds with high contrast and accessibility, specifically designed for validation workflows:

```mermaid
graph TD
    A[Clerk Action] --> B{Decision Point}
    B --> C[Validation Process]
    C --> D[Action/Workflow]
    D --> E[Success/Endpoint]
    E --> F[Confidence Assessment]
    F --> G[Review Process]
    G --> H[Manual Operation]
    H --> I[Batch Operation]
    I --> J[Annotation Tool]
    J --> K[Completion Status]
    K --> L[Rejection/Error]

    classDef clerk fill:#4A90E2,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef decision fill:#FF9500,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef validation fill:#7ED321,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef action fill:#9013FE,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef endpoint fill:#50E3C2,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef confidence fill:#00B894,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef review fill:#F5A623,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef manual fill:#BD10E0,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef batch fill:#6C5CE7,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef tool fill:#74B9FF,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef completion fill:#E17055,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef rejection fill:#D0021B,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF

    class A clerk
    class B decision
    class C validation
    class D action
    class E endpoint
    class F confidence
    class G review
    class H manual
    class I batch
    class J tool
    class K completion
    class L rejection
```

### Validation Workflow Color Code Reference:

| Color              | Hex Code  | Purpose               | Text Color | Validation Context                                  |
| ------------------ | --------- | --------------------- | ---------- | --------------------------------------------------- |
| 🔵 **Blue**        | `#4A90E2` | Clerk Actions         | White      | Login, dashboard navigation, user interactions      |
| 🟠 **Orange**      | `#FF9500` | Decision Points       | Black      | Document availability, field accuracy checks        |
| 🟢 **Light Green** | `#7ED321` | Validation Processes  | Black      | Field validation, data verification, OCR confidence |
| 🟣 **Purple**      | `#9013FE` | Action/Workflow Steps | White      | Approve, reject, escalate, save operations          |
| 🔷 **Teal**        | `#50E3C2` | Success/Endpoints     | Black      | Completed validations, ERP integration              |
| 🟢 **Dark Green**  | `#00B894` | Confidence Assessment | White      | High/medium/low confidence indicators               |
| 🟡 **Yellow**      | `#F5A623` | Review Processes      | Black      | Manual review, supervisor escalation                |
| 🟣 **Magenta**     | `#BD10E0` | Manual Operations     | White      | Manual data entry, corrections, verification        |
| 🟣 **Indigo**      | `#6C5CE7` | Batch Operations      | White      | Batch processing, bulk approvals                    |
| 🔵 **Light Blue**  | `#74B9FF` | Annotation Tools      | White      | Document annotation, highlighting, comments         |
| 🔸 **Coral**       | `#E17055` | Completion Status     | White      | Document complete, export, reporting                |
| 🔴 **Red**         | `#D0021B` | Rejection/Error       | White      | Document rejection, processing errors               |

### Validation-Specific Design Principles:

- **Clerk Focus**: Blue for clerk actions emphasizes user-driven processes
- **Validation Clarity**: Green shades distinguish validation confidence levels
- **Action Distinction**: Purple variants separate different action types
- **Error Emphasis**: Red highlights rejection and error states for immediate attention
- **Review Workflow**: Yellow indicates items requiring human review
- **Batch Processing**: Indigo provides clear identification of batch operations
- **Tool Integration**: Light blue represents annotation and document tools
- **High Contrast**: All colors provide excellent visibility on dark backgrounds
- **Accessibility**: Compliant with WCAG 2.1 AA standards for color contrast
- **Semantic Workflow**: Colors follow the logical flow of validation processes

### Usage Guidelines:

- **Confidence Levels**: Use green variants to show AI confidence assessments
- **Review States**: Yellow indicates manual review requirements
- **Completion Flow**: Coral shows successful completion and export states
- **Error Handling**: Red provides clear indication of rejection workflows
- **Batch Operations**: Indigo distinguishes batch from individual document processing

_This diagram is part of the Apex IDP UI Flow Documentation_
_Last updated: [Current Date]_
