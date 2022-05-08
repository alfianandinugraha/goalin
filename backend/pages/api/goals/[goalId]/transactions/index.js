import Ajv from "ajv";
import transactionServices from "services/transaction";
import response from "utils/constants/messages/response";
import { transactionBodySchema } from "utils/constants/schemas/transaction";
import { connectAuth } from "utils/helpers/connect";

const ajv = new Ajv();

export default connectAuth().post(async (req, res) => {
  const validate = ajv.compile(transactionBodySchema);

  if (!validate(req.body)) {
    return res.status(400).json(response.INVALID_BODY);
  }

  const goalId = req.query.goalId;
  try {
    const transaction = await transactionServices.create({
      goalId,
      ...req.body,
    });

    return res.json({
      message: "Berhasil menyimpan transaction",
      payload: transaction,
    });
  } catch (err) {
    return res.status(400).json({
      message: err.message,
      payload: {},
    });
  }
});
