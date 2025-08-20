# apex-integration

Purpose: ERP and external system integrations.

Capabilities:
- Spring Integration flows (HTTP adapters, transformers)
- WebFlux clients for non-blocking outbound I/O

Dependencies:
- `apex-core`, Spring Web, WebFlux, Spring Integration

Testing:
- Unit: transformers and services
- Integration: HTTP stubbed endpoints; backpressure behavior
