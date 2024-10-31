### JSON Schema Validation

We store Json Schema with an Api Identifier in DB where schema is of type json.

Client send a request with an Api Identifier and Data of json type.

The validator validates the client json data against the schema stored in DB if the Api Identifier matched.
