package projects.hobbes.team.reminderapp.messenger;

/**
 * Created by Daniel on 3/31/2016.
 */
public class Message
{
    private String sender;
    private String body;
    private boolean read;

    public Message(String sender, String body, String read)
    {
        this.sender = sender;
        this.body = body;

        if(read.equals("0"))
            this.read = false;
        else
            this.read = true;

    }

    public String getSender()
    {
        return sender;
    }

    public String getBody()
    {
        return body;
    }

    public boolean getRead()
    {
        return read;
    }
}
