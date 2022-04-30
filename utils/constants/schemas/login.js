import { JSONSchemaType } from "ajv";

/**
 * @typedef LoginBody
 * @property {string} email
 * @property {string} password
 */

/**
 * @type {JSONSchemaType<LoginBody>}
 */
const loginBodySchema = {
  type: "object",
  properties: {
    email: { type: "string", format: "email" },
    password: { type: "string" },
  },
  required: ["email", "password"],
};

export { loginBodySchema };
