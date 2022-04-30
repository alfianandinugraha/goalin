import bcrypt from "bcryptjs";

/**
 * @param {string} text
 */
const hash = (text) => {
  return bcrypt.hashSync(text, 8);
};

const hashServices = {
  hash,
};

export default hashServices;
