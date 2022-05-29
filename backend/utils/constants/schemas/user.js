import { JSONSchemaType } from "ajv";

/**
 * @typedef UpdateUserBodySchema
 * @property {string} fullName
 * @property {string} email
 */

/**
 * @type {JSONSchemaType<UpdateUserBodySchema>}
 */
const updateUserBodySchema = {
  type: "object",
  properties: {
    email: {
      type: "string",
    },
    fullName: {
      type: "string",
    },
  },
  required: ["email", "fullName"],
};

export { updateUserBodySchema };
