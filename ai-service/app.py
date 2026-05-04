import os
import logging
from flask import Flask, request, jsonify
from groq import Groq
from dotenv import load_dotenv

load_dotenv()

app = Flask(__name__)
logging.basicConfig(level=logging.INFO)

groq_client = Groq(api_key=os.environ.get("GROQ_API_KEY", "dummy_key"))

def get_groq_completion(prompt):
    try:
        chat_completion = groq_client.chat.completions.create(
            messages=[{"role": "user", "content": prompt}],
            model="llama3-8b-8192",
        )
        return chat_completion.choices[0].message.content
    except Exception as e:
        logging.error(f"Groq API error: {e}")
        return None

@app.route('/health', methods=['GET'])
def health_check():
    return jsonify({"status": "healthy"}), 200

@app.route('/describe', methods=['POST'])
def describe_penalty():
    data = request.json
    title = data.get('title', '')
    description = data.get('description', '')
    
    prompt = f"Analyze the following regulatory penalty and provide a concise summary of its severity and implications:\nTitle: {title}\nDescription: {description}"
    
    response = get_groq_completion(prompt)
    if response:
        return jsonify({"description": response}), 200
    else:
        return jsonify({"description": "AI Service currently unavailable. Fallback: Severity needs manual review based on the description."}), 200

@app.route('/recommend', methods=['POST'])
def recommend_actions():
    data = request.json
    title = data.get('title', '')
    description = data.get('description', '')
    
    prompt = f"Based on this regulatory penalty, list 3 actionable recommendations for a company to avoid similar penalties in the future:\nTitle: {title}\nDescription: {description}"
    
    response = get_groq_completion(prompt)
    if response:
        return jsonify({"recommendations": response}), 200
    else:
        return jsonify({"recommendations": "AI Service unavailable. Standard fallback: 1. Review internal policies. 2. Conduct compliance training. 3. Perform a risk audit."}), 200

@app.route('/generate-report', methods=['POST'])
def generate_report():
    data = request.json
    penalties = data.get('penalties', [])
    
    prompt = f"Generate a brief executive summary report based on the following list of recent regulatory penalties: {penalties}"
    
    response = get_groq_completion(prompt)
    if response:
        return jsonify({"report": response}), 200
    else:
        return jsonify({"report": "AI Service unavailable. Fallback: Multiple regulatory penalties have been recorded recently requiring executive attention."}), 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
