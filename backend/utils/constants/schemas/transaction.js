import { JSONSchemaType } from "ajv";

/**
 * @typedef CreateTransactionBody
 * @property {number} amount
 * @property {string} walletId
 * @property {number} createdAt
 */

/**
 * @type {JSONSchemaType<CreateTransactionBody>}
 */
const transactionBodySchema = {
  type: "object",
  properties: {
    amount: { type: "number" },
    walletId: { type: "string" },
    createdAt: { type: "number" },
  },
  required: ["amount", "walletId", "createdAt"],
};

export { transactionBodySchema };
