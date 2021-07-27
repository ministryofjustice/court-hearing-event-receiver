# court-hearing-event-receiver
[![Swagger API docs (needs VPN)](https://img.shields.io/badge/API_docs_(needs_VPN)-view-85EA2D.svg?logo=swagger)](https://court-hearing-event-receiver-dev.hmpps.service.justice.gov.uk/swagger-ui.html#)
[![CircleCI](https://circleci.com/gh/ministryofjustice/court-hearing-event-receiver.svg?style=svg)](https://circleci.com/gh/ministryofjustice/court-hearing-event-receiver)

The service will receive court hearing events from CP and publish these to SNS.

Outstanding Questions

1. If roomName is not predictable or close to what we want, Does roomId link to something which we could derive the values we are after, like "1", "2", etc ?
2. Only id is required in `courtCentre` which means we are missing the code, room (id or name)
3. `hearingDays` is not a required field in hearing.  It has a min sze of 1 if it is present but it might not be. Under what circumstances is this true for us ?
4. `prosecutionCases` is not a required field in hearing.  It has a min sze of 1 if it is present but it might not be. Under what circumstances is this true for us ?
4. The "type" in `hearing`. There is no enumeration on the possible types. I have seen "Sentence" and "First hearing" in the samples but what is possible ?
5. What do we need to understand the case lifecycle ? For example, are any of these useful ?
   `caseStatus` - seen as READY_FOR_REVIEW, ACTIVE but it's just a string so difficult to know
6. In `defendant`, there's a `prosecutionCaseId` - that seems to just duplicate the id in the parent `prosecutionCase` object. Is that always the case ?


