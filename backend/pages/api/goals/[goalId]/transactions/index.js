import Ajv from "ajv";
import goalServices from "services/goal";
import transactionServices from "services/transaction";
import response from "utils/constants/messages/response";
import { transactionBodySchema } from "utils/constants/schemas/transaction";
import { connectAuth } from "utils/helpers/connect";

const ajv = new Ajv();

export default connectAuth()
  .get(async (req, res) => {
    const goalId = req.query.goalId;

    try {
      await goalServices.get({ goalId, userId: req.user.id });
      const transactions = await transactionServices.getAll(goalId);

      return res.json({
        message: "Berhasil mendapatkan list transactions",
        payload: transactions.map((item) => {
          return {
            id: item.transaction_id,
            amount: item.amount,
            createdAt: item.created_at,
            goalId: item.goal_id,
            wallet: {
              id: item.wallet.wallet_id,
              name: item.wallet.name,
            },
          };
        }),
      });
    } catch (err) {
      return res.status(400).json({
        message: err.message,
        payload: {},
      });
    }
  })
  .post(async (req, res) => {
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
