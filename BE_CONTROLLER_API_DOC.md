# Backend API Controller Documentation

This document summarizes the core controller responsibilities and endpoint contracts in the `be_java_social` backend.

## AuthController
Base path: `/api/auth`

- `POST /login`
  - Body: raw JSON login payload
  - Returns: login response from `AuthService`

- `POST /logout`
  - Returns: logout success message

- `POST /register`
  - Body: raw JSON register payload
  - Requires: current user must be admin
  - Registers a new user in the current workspace

- `POST /admin/register`
  - Body: raw JSON register payload
  - Registers a new workspace and admin user

## BuildingController
Base path: `/api/buildings`

- `POST /`
  - Body: `AddOrUpdateBuildingDto` JSON
  - Adds a new building for the current workspace

- `GET /workspace/{workspaceId}`
  - Returns all buildings for the given workspace

- `GET /{id}`
  - Returns details for a single building

- `PUT /update/{id}`
  - Body: `AddOrUpdateBuildingDto` JSON
  - Updates building name and address

- `DELETE /delete/{id}`
  - Deletes the building by ID

## ContractController
Base path: `/api/contracts`

- `GET /{id}`
  - Returns contract details by contract ID

- `GET /room/{roomId}`
  - Returns all contracts for a room

- `GET /tenant/{tenantId}`
  - Returns all contracts for a tenant

- `PUT /{id}`
  - Body: contract update JSON
  - Updates status, deposit, start/end dates

- `PUT /{id}/extend?duration={duration}`
  - Extends contract end date by duration string (e.g. `6M`)

- `DELETE /{id}`
  - Deletes the contract

## FileController
Base path: `/api/files`

- `POST /upload`
  - Accepts multipart files under `file`
  - Returns JSON array of uploaded URLs

## InvoiceController
Base path: `/api/invoices`

- `GET /workspace`
  - Returns invoices for the current workspace

- `GET /room/{roomId}`
  - Returns invoices for a given room

- `GET /{id}`
  - Returns a single invoice

- `PUT /{id}/pay?amount={amount}`
  - Applies payment to an invoice

- `PUT /{id}`
  - Body: invoice update JSON
  - Updates total amount and status

- `DELETE /{id}`
  - Deletes invoice by ID

## MeterReadingController
Base path: `/api/meter-readings`

- `POST /`
  - Body: meter reading JSON
  - Validates and prevents duplicate month/year entries

- `GET /room/{roomId}`
  - Returns reading history for a room

- `PUT /{id}`
  - Body: meter reading JSON
  - Updates recorded meter values

- `DELETE /{id}`
  - Deletes a meter reading entry

## ProfileController
Base path: `/api/profile`

- `GET /`
  - Returns current authenticated user's profile

- `PUT /`
  - Body: raw profile JSON
  - Updates current user's profile

## RoomController
Base path: `/api/rooms`

- `POST /`
  - Body: `AddOrUpdateRoomDto` JSON
  - Adds a new room to the current workspace

- `GET /building/{buildingId}`
  - Returns rooms inside a building

- `GET /{id}`
  - Returns details of a room

- `PUT /{id}`
  - Body: `AddOrUpdateRoomDto` JSON
  - Updates room fields

- `DELETE /{id}`
  - Deletes a room by ID

## RoomServiceController
Base path: `/api/room-services`

- `POST /assign`
  - Body: room service assignment JSON
  - Assigns a service to a room

- `GET /room/{roomId}`
  - Returns services assigned to a room

- `DELETE /room/{roomId}/service/{serviceId}`
  - Removes a service from a room

## ServiceCatalogController
Base path: `/api/service-catalogs`

- `POST /`
  - Body: service catalog JSON
  - Adds a new service definition to current workspace

- `GET /workspace`
  - Returns service catalog entries for workspace

- `PUT /{id}`
  - Body: service update JSON
  - Updates name, price, unit

- `DELETE /{id}`
  - Deletes a service catalog entry

## TenantController
Base path: `/api/tenants`

- `POST /`
  - Body: `AddTenantWithContractDto` JSON
  - Creates tenant and contract together

- `PUT /{id}`
  - Body: tenant update JSON
  - Updates tenant profile

- `GET /{id}`
  - Returns tenant details

- `DELETE /{id}`
  - Deletes tenant by ID

---

### Recommended API cleanup actions

- Prefer RESTful route naming with resource IDs under the base path, e.g. `PUT /api/contracts/{id}` and `DELETE /api/contracts/{id}`.
- Keep consistent response semantics: success JSON objects and HTTP 404 for missing resources.
- Use typed DTO request bodies instead of raw `String` payloads where possible.
- Add `@CrossOrigin` if a separate front-end consumes this backend from another origin.
