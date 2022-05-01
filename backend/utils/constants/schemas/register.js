import { JSONSchemaType } from "ajv";

/**
 * @typedef RegisterBody
 * @property {string} email
 * @property {string} fullName
 * @property {string} password
 */

/**
 * @type {JSONSchemaType<RegisterBody>}
 */
const registerBodySchema = {
  type: "object",
  properties: {
    email: { type: "string", format: "email" },
    password: { type: "string" },
    fullName: { type: "string" },
  },
  required: ["email", "password", "fullName"],
};

export { registerBodySchema };
