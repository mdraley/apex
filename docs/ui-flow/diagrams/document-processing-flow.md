# Document Upload and Processing Flow

This diagram shows the complete document upload, processing, and validation workflow in the Apex IDP platform.

## Document Upload Flow

```mermaid
graph TD
    A[User Selects Files] --> B[Drag & Drop or Browse]
    B --> C[File Validation]

    C --> D{File Valid?}
    D -->|No| E[Show Error Message]
    D -->|Yes| F[Upload to Storage]

    E --> G[User Corrects File]
    G --> B

    F --> H[Generate Upload Progress]
    H --> I[File Upload Complete]
    I --> J[Create Document Record]

    J --> K[Queue for Processing]
    K --> L[OCR Processing]

    L --> M{OCR Success?}
    M -->|No| N[Mark as Failed]
    M -->|Yes| O[AI Classification]

    O --> P{Classification Confidence}
    P -->|High| Q[Auto-classify]
    P -->|Low| R[Manual Classification Queue]

    Q --> S[Data Extraction]
    R --> T[Human Classification]
    T --> S

    S --> U[AI Data Extraction]
    U --> V{Extraction Confidence}

    V -->|High| W[Auto-approval]
    V -->|Medium| X[Review Queue]
    V -->|Low| Y[Manual Validation Queue]

    W --> Z[ERP Integration]
    X --> AA[AP Clerk Review]
    Y --> AA

    AA --> BB{Approved?}
    BB -->|Yes| Z
    BB -->|No| CC[Rejection Workflow]

    CC --> DD[Notify Vendor]
    Z --> EE[Payment Processing]
    N --> FF[Error Notification]

    classDef userAction fill:#4A90E2,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef validation fill:#7ED321,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef processing fill:#9013FE,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef decision fill:#FF9500,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef endpoint fill:#50E3C2,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef error fill:#D0021B,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF

    class A,B,G userAction
    class C,L,O,S,U validation
    class F,H,I,J,K processing
    class D,M,P,V,BB decision
    class Z,EE endpoint
    class E,N,FF,CC,DD error
```

## Batch Processing Flow

```mermaid
graph TD
    A[Multiple Files Selected] --> B[Create Batch]
    B --> C[Validate All Files]

    C --> D[Upload Files in Parallel]
    D --> E[Track Batch Progress]

    E --> F{All Files Uploaded?}
    F -->|No| G[Continue Upload]
    F -->|Yes| H[Start Batch Processing]

    G --> F

    H --> I[Process Documents in Parallel]
    I --> J[OCR All Documents]
    J --> K[Classify All Documents]
    K --> L[Extract Data from All]

    L --> M[Calculate Batch Statistics]
    M --> N{Batch Quality Score}

    N -->|High| O[Auto-approve Batch]
    N -->|Medium| P[Partial Review Required]
    N -->|Low| Q[Full Review Required]

    O --> R[Bulk ERP Integration]
    P --> S[Review Low-confidence Items]
    Q --> T[Review All Items]

    S --> U{Partial Review Complete?}
    T --> V{Full Review Complete?}

    U -->|Yes| R
    V -->|Yes| R

    R --> W[Batch Complete]

    classDef batch fill:#BD10E0,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef processing fill:#9013FE,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef review fill:#F5A623,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef decision fill:#FF9500,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef endpoint fill:#50E3C2,stroke:#FFFFFF,stroke-width:2px,color:#000000

    class A,B,C,D,E,M batch
    class I,J,K,L processing
    class P,Q,S,T,U,V review
    class F,N decision
    class O,R,W endpoint
```

## Real-time Processing Updates

```mermaid
sequenceDiagram
    participant U as User
    participant F as Frontend
    participant B as Backend
    participant Q as Queue
    participant P as Processor
    participant W as WebSocket

    U->>F: Upload Document
    F->>B: POST /api/documents/upload
    B->>B: Save File to Storage
    B->>Q: Add to Processing Queue
    B->>F: Return Upload Success + Document ID
    F->>W: Subscribe to Document Updates

    Q->>P: Dequeue Document
    P->>P: Start OCR Processing
    P->>W: Emit 'processing_started'
    W->>F: Notify Processing Started
    F->>U: Show "Processing..." Status

    P->>P: Complete OCR
    P->>P: Start AI Classification
    P->>W: Emit 'ocr_complete'
    W->>F: Notify OCR Complete
    F->>U: Update Progress (33%)

    P->>P: Complete Classification
    P->>P: Start Data Extraction
    P->>W: Emit 'classification_complete'
    W->>F: Notify Classification Complete
    F->>U: Update Progress (66%)

    P->>P: Complete Data Extraction
    P->>B: Save Extracted Data
    P->>W: Emit 'extraction_complete'
    W->>F: Notify Extraction Complete
    F->>U: Update Progress (100%)

    alt High Confidence
        P->>B: Auto-approve Document
        P->>W: Emit 'auto_approved'
        W->>F: Notify Auto-approved
        F->>U: Show "Approved" Status
    else Low Confidence
        P->>Q: Add to Review Queue
        P->>W: Emit 'needs_review'
        W->>F: Notify Needs Review
        F->>U: Show "Pending Review" Status
    end
```

## Error Handling and Recovery

```mermaid
graph TD
    A[Processing Error Occurs] --> B{Error Type}

    B -->|Upload Error| C[File Upload Failed]
    B -->|OCR Error| D[OCR Processing Failed]
    B -->|AI Error| E[Classification/Extraction Failed]
    B -->|Storage Error| F[Storage System Error]
    B -->|Network Error| G[Network Connectivity Error]

    C --> H[Retry Upload]
    D --> I[Retry OCR with Different Engine]
    E --> J[Fallback to Manual Processing]
    F --> K[Retry with Backup Storage]
    G --> L[Queue for Later Retry]

    H --> M{Retry Success?}
    I --> M
    J --> N[Add to Manual Queue]
    K --> M
    L --> O[Exponential Backoff]

    M -->|Yes| P[Continue Processing]
    M -->|No| Q{Max Retries Reached?}

    Q -->|No| R[Increment Retry Count]
    Q -->|Yes| S[Mark as Failed]

    R --> O
    O --> T[Wait Period]
    T --> H

    S --> U[Notify User of Failure]
    U --> V[Provide Manual Options]

    V --> W[Manual Upload]
    V --> X[Contact Support]
    V --> Y[Skip Document]

    N --> Z[AP Clerk Manual Processing]
    P --> AA[Normal Processing Flow]

    classDef error fill:#D0021B,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef retry fill:#F5A623,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef manual fill:#7ED321,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef success fill:#50E3C2,stroke:#FFFFFF,stroke-width:2px,color:#000000

    class A,C,D,E,F,G,S,U error
    class H,I,K,L,M,Q,R,O,T retry
    class J,N,V,W,X,Y,Z manual
    class P,AA success
```

## File Format Support and Validation

```mermaid
graph TD
    A[File Selected] --> B[Check File Extension]

    B --> C{Supported Format?}
    C -->|PDF| D[Validate PDF Structure]
    C -->|PNG/JPG| E[Validate Image Format]
    C -->|TIFF| F[Validate TIFF Format]
    C -->|Unsupported| G[Reject File]

    D --> H{PDF Valid?}
    E --> I{Image Valid?}
    F --> J{TIFF Valid?}

    H -->|Yes| K[Check PDF Security]
    H -->|No| L[PDF Corruption Error]
    I -->|Yes| M[Check Image Quality]
    I -->|No| N[Image Format Error]
    J -->|Yes| O[Check TIFF Pages]
    J -->|No| P[TIFF Format Error]

    K --> Q{Password Protected?}
    Q -->|Yes| R[Request Password]
    Q -->|No| S[Check File Size]

    M --> T{Quality Sufficient?}
    T -->|Yes| S
    T -->|No| U[Low Quality Warning]

    O --> V{Multi-page?}
    V -->|Yes| W[Process All Pages]
    V -->|No| S

    R --> X[User Enters Password]
    X --> Y{Password Correct?}
    Y -->|Yes| S
    Y -->|No| Z[Password Error]

    S --> AA{Size Within Limits?}
    AA -->|Yes| BB[File Accepted]
    AA -->|No| CC[File Too Large Error]

    U --> DD[User Confirms Proceed]
    DD --> S

    L --> EE[Show Error Message]
    N --> EE
    P --> EE
    Z --> EE
    CC --> EE
    G --> EE

    W --> BB
    BB --> FF[Queue for Processing]

    classDef validation fill:#7ED321,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef success fill:#50E3C2,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef error fill:#D0021B,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef decision fill:#FF9500,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef userInput fill:#4A90E2,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF

    class B,D,E,F,H,I,J,K,M,O,S,T,V,AA validation
    class BB,FF success
    class G,L,N,P,U,Z,CC,EE error
    class C,Q,Y decision
    class R,X,DD userInput
```

## Processing Queue Management

```mermaid
graph TD
    A[Document Uploaded] --> B[Add to Processing Queue]

    B --> C{Queue Type}
    C -->|High Priority| D[Priority Queue]
    C -->|Normal| E[Standard Queue]
    C -->|Batch| F[Batch Queue]

    D --> G[Immediate Processing]
    E --> H[FIFO Processing]
    F --> I[Batch Processing]

    G --> J[Assign to Available Worker]
    H --> K{Workers Available?}
    I --> L{Batch Size Threshold?}

    K -->|Yes| J
    K -->|No| M[Wait in Queue]

    L -->|Yes| N[Start Batch Processing]
    L -->|No| O[Wait for More Documents]

    J --> P[Worker Processing Document]
    M --> Q[Check Queue Periodically]
    N --> R[Parallel Batch Processing]
    O --> S[Batch Timer Check]

    Q --> K
    S --> T{Timer Expired?}
    T -->|Yes| N
    T -->|No| O

    P --> U{Processing Complete?}
    U -->|Success| V[Move to Validation Queue]
    U -->|Error| W[Move to Error Queue]
    U -->|In Progress| P

    R --> X[Individual Document Processing]
    X --> Y{All Documents Complete?}
    Y -->|No| X
    Y -->|Yes| Z[Batch Complete]

    V --> AA[Notify AP Clerks]
    W --> BB[Notify Administrators]
    Z --> CC[Notify Batch Completion]

    classDef queue fill:#BD10E0,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef processing fill:#9013FE,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef completion fill:#50E3C2,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef error fill:#D0021B,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef decision fill:#FF9500,stroke:#FFFFFF,stroke-width:2px,color:#000000

    class B,C,D,E,F,M,Q queue
    class G,H,I,J,P,R,X processing
    class V,Z,AA,CC completion
    class W,BB error
    class K,L,T,U,Y decision
```

---

## Dark Mode Color Legend

The following color scheme has been optimized for dark backgrounds with high contrast and accessibility in mind:

```mermaid
graph TD
    A[User Action] --> B[Validation Process]
    B --> C[System Processing]
    C --> D{Decision Point}
    D --> E[Success/Endpoint]
    D --> F[Error State]
    F --> G[Retry/Recovery]
    G --> H[Manual Intervention]
    H --> I[Batch Operation]
    I --> J[Review Process]

    classDef userAction fill:#4A90E2,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef validation fill:#7ED321,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef processing fill:#9013FE,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef decision fill:#FF9500,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef endpoint fill:#50E3C2,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef error fill:#D0021B,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef retry fill:#F5A623,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef manual fill:#7ED321,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef batch fill:#BD10E0,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef review fill:#F5A623,stroke:#FFFFFF,stroke-width:2px,color:#000000

    class A userAction
    class B validation
    class C processing
    class D decision
    class E endpoint
    class F error
    class G retry
    class H manual
    class I batch
    class J review
```

### Color Code Reference:

| Color          | Hex Code  | Purpose                           | Text Color |
| -------------- | --------- | --------------------------------- | ---------- |
| 🔵 **Blue**    | `#4A90E2` | User Actions & Input              | White      |
| 🟢 **Green**   | `#7ED321` | Validation & Manual Processes     | Black      |
| 🟣 **Purple**  | `#9013FE` | System Processing                 | White      |
| 🟠 **Orange**  | `#FF9500` | Decision Points                   | Black      |
| 🔷 **Teal**    | `#50E3C2` | Success States & Endpoints        | Black      |
| 🔴 **Red**     | `#D0021B` | Error States                      | White      |
| 🟡 **Yellow**  | `#F5A623` | Retry/Recovery & Review Processes | Black      |
| 🟣 **Magenta** | `#BD10E0` | Batch Operations                  | White      |

### Design Principles:

- **High Contrast**: All colors provide sufficient contrast against dark backgrounds
- **Accessibility**: Compliant with WCAG 2.1 AA standards for color contrast
- **Semantic Meaning**: Each color represents a specific type of operation or state
- **Consistency**: Same color types are used across all diagrams for visual coherence
- **Text Legibility**: Text colors (black/white) chosen for optimal readability

_This diagram is part of the Apex IDP UI Flow Documentation_
_Last updated: [Current Date]_
