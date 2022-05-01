import { JSONSchemaType } from "ajv";

/**
 * @typedef CreateGoalBody
 * @property {string} name
 * @property {string} categoryId
 * @property {number} total
 * @property {string} notes
 */

/**
 * @type {JSONSchemaType<CreateGoalBody>}
 */
const createGoalSchema = {
  type: "object",
  properties: {
    name: {
      type: "string",
    },
    categoryId: {
      type: "string",
    },
    total: {
      type: "number",
    },
    note: {
      type: "string",
    },
  },
  required: ["name", "categoryId", "total"],
};

export { createGoalSchema };
