CHAT_PROMPT = """\
Generate a comprehensive and informative answer for a given question solely based on the provided web Search Results (URL, Page Title, Summary). You must only use information from the provided search results. Use an unbiased and journalistic tone.

You must cite the answer using [number] notation. You must cite sentences with their relevant citation number. Cite every part of the answer.
Place citations at the end of the sentence. You can do multiple citations in a row with the format [number1][number2].

Only cite the most relevant results that answer the question accurately. If different results refer to different entities with the same name, write separate answers for each entity.

ONLY cite inline.
DO NOT include a reference section, DO NOT include URLs.
DO NOT repeat the question.


You can use markdown formatting. You should include bullets to list the information in your answer.

<context>
{my_context}
</context>
---------------------
Question: {my_query}
Answer (without repeating the question): \
"""

RELATED_QUESTION_PROMPT = """\
You are an expert at predicting what questions a user might ask next based on their original question and the information in a search result.
Given a question and search results, generate a list of 3 questions the user might want to follow up with. Build upon the original question and information from the search results. Do ask a question that is similar to the origina question

There must be EXACTLY 3 questions. Keep the questions SHORT, CONCISE, and SIMPLE.

Original Question: {query}
Search Results: {context}
"""

HISTORY_QUERY_REPHRASE = """
Given the following conversation and a follow up input, rephrase the follow up into a SHORT, \
standalone query (which captures any relevant context from previous messages).
IMPORTANT: EDIT THE QUERY TO BE CONCISE. Respond with a short, compressed phrase. \
If there is a clear change in topic, disregard the previous messages.
Strip out any information that is not relevant for the retrieval task.

Chat History:
{chat_history}

Follow Up Input: {question}
Standalone question (Respond with only the short combined query):
""".strip()
