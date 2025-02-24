- add client model, name, username, phone no
- vehicle mode: Make, Model, Trim, year, plate
- add client vehicles model: id, client_id, vehicile id
- add booking request model:
request id
requested_by client
date request was created, 
status=(CREATED, RESPONDER_ASSIGNED, RESPONDER_ON_WAY, RESPONDER_REACHED, 
SERVICE_IN_PROGRESS, SERVICE_DONE_AWAITING_PAYMENT, COMPLETED, CANCELLED)
vehicle,
detail description,
priority=NOW, NEXT_BUSINESS_DAY, NEXT_DAY
address
service type: TOWING, BATTERY, FUEL, TIRE, LOCK, MINOR_REPAIR more to come

