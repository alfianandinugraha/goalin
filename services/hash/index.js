import bcrypt from "bcryptjs";

/**
 * @param {string} text
 */
const hash = (text) => {
  return bcrypt.hashSync(text, 8);
};

/**
 * @param {string} text
 * @param {string} hash
 */
const compare = (text, hash) => {
  return bcrypt.compareSync(text, hash);
};

const hashServices = {
  hash,
  compare,
};

export default hashServices;
