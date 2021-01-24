package com.example.tp_eb03;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;


/**
 * classe Custom View crée pour avoir une barre de progression de forme circulaire.
 * Cet élément va entre 0 et 100% et commence par le haut du cercle.
 * Il est impossible de dépasser 100% ou d'avoir des chiffres négatifs.
 * Quand on dépasse 100% par la droite, le curseur reste à 100% et inversement si on dépasse 0%
 * les dimensions, couleurs, La présence de texte est modifiable dans le xml
 */
public class CustomView extends View {

    /*
     * le diamètre des trois cercles.
     */
    private int mExteriorCircleDiameter;
    private int mMiddleCircleDiameter;
    private int mInnerCircleDiameter;

    //variable qui permet de s'assurer qu'il y a bien un double click et pas un simple.
    private boolean mDoubleClick=false;
    //variable qui est de l'instance de l'interface que nous avons crée ici et qui permet "écouter"
    //les changements, les evènements qui ont lieu sur la vue
    private CustomViewChangeListener mCustomViewChangeListener;
    // point central du canvas. Utile pour la recherche du non-dépassement de 100% à droite
    private Point mCenterPoint;

    //enable
        //permet la présence des traits long. peut etre enlevé avec l'XML
    private boolean mEnableLongLines=true;
        //permet la présence des traits courts. peut etre enlevé avec l'XML
    private boolean mEnableLittleLines=true;
        //permet d'avoir une appli qui est utilisable si activé sinon tout est gris
    private boolean mEnabled =true;
        //permet d'avoir la valeur du curseur au milieu du cercle.
    private boolean mEnableText = true;


    //disable
        // empêche l'update de mouvement quand on fait un double click.
    private boolean mDisabledMove=false;


    //colors
    private int mExteriorCircleColor;
    private int mMiddleCircleColor;
    private int mInnerCircleColor;
    private int mDisabledColor;
    private int mLittleLinesColor;
    private int mLongLinesColor;
    private int mTextColor;
    private int mValueMiddleCircleColor;

    //attributs de pinceaux
    private Paint mMiddleCirclePaint;
    private Paint mValueMiddleCirclePaint;
    private Paint mExteriorCirclePaint;
    private Paint mInnerCirclePaint;
    private Paint mLittleLinesPaint;
    private Paint mLongLinesPaint;
    private Paint mTextPaint;

    public void setmValue(float mValue) {
        this.mValue = mValue;
    }

    // values
        // valeur courante du curseur en pourcent.
    private float mValue = 0;
        // entre 0 et 100
    private float mMin = 0;
    private float mMax = 100;



    //valeur non changeables
    private final static float MIN_EXTERIOR_CIRCLE_DIAMETER = 130;
    private final static float MIN_MIDDLE_CIRCLE_DIAMETER = 120;
    private final static float MIN_INNER_CIRCLE_DIAMETER = 60;

    // valeur par défaut
    private final static float DEFAULT_EXTERIOR_CIRCLE_DIAMETER = 160;
    private final static float DEFAULT_MIDDLE_CIRCLE_DIAMETER = 140;
    private final static float DEFAULT_INNER_CIRCLE_DIAMETER = 80;



    /**
     * Constructeur du custom View n'utilisant pas d'attributs.
     * Si le XML est vide, c'est celui-ci qu'on utilise.
     * Les deux constructeurs sont appelés par la méthode init
     * en fonction de s'il y a des attributs ou non
     * @param context
     */
    public CustomView(Context context) {
        super(context);
        init(context,null);
    }

    /**
     * Constructeur du custom View utilisant les attributs qu'il trouvera dans le fichier XML associé
     * Les deux constructeurs sont appelés par la méthode init
     * en fonction de s'il y a des attributs ou non
     * @param context
     * @param attrs
     */
    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    /**
     * Setter du CustomViewChangeListener
     * @param mCustomViewChangeListener
     */
    public void setCustomViewChangeListener(CustomViewChangeListener mCustomViewChangeListener) {
        this.mCustomViewChangeListener = mCustomViewChangeListener;
    }

    /**
     * fonction d'initialisation des variables de types Paint, Color, du diamètre des cercles et autres attributs du xml
     * du style et de la taille des différents traits...
     * @param context
     * @param attrs
     */
    private void init(Context context,AttributeSet attrs) {
        mMiddleCirclePaint = new Paint();
        mValueMiddleCirclePaint = new Paint();
        mExteriorCirclePaint = new Paint();
        mInnerCirclePaint = new Paint();
        mLittleLinesPaint = new Paint();
        mLongLinesPaint = new Paint();
        mTextPaint=new Paint();

        //couleurs
        mInnerCircleColor = ContextCompat.getColor(context,R.color.clear_green);
        mMiddleCircleColor = ContextCompat.getColor(context,R.color.dark_green);
        mExteriorCircleColor = ContextCompat.getColor(context,R.color.grey_black);
        mValueMiddleCircleColor=  ContextCompat.getColor(context,R.color.grey);
        mDisabledColor = ContextCompat.getColor(context,R.color.grey);
        mLittleLinesColor = ContextCompat.getColor(context,R.color.black);
        mLongLinesColor = ContextCompat.getColor(context,R.color.black);
        mTextColor= ContextCompat.getColor(context,R.color.white);

        mInnerCircleDiameter = (int)dpToPixel(DEFAULT_INNER_CIRCLE_DIAMETER);
        mMiddleCircleDiameter= (int) dpToPixel(DEFAULT_MIDDLE_CIRCLE_DIAMETER);
        mExteriorCircleDiameter= (int) dpToPixel(DEFAULT_EXTERIOR_CIRCLE_DIAMETER);

         if(attrs !=  null){
            TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.CustomView,0,0);
            mExteriorCircleDiameter = (int) attr.getDimension(R.styleable.CustomView_ExteriorCircleDiameter,mExteriorCircleDiameter);
            mExteriorCircleColor = attr.getColor(R.styleable.CustomView_ExteriorCircleColor,mExteriorCircleColor);
            mMiddleCircleDiameter = (int) attr.getDimension(R.styleable.CustomView_MiddleCircleDiameter,mMiddleCircleDiameter);
            mMiddleCircleColor = attr.getColor(R.styleable.CustomView_MiddleCircleColor,mMiddleCircleColor);
            mInnerCircleDiameter = (int) attr.getDimension(R.styleable.CustomView_InnerCircleDiameter,mInnerCircleDiameter);
            mInnerCircleColor = attr.getColor(R.styleable.CustomView_InnerCircleColor,mInnerCircleColor);
            mTextColor = attr.getColor(R.styleable.CustomView_TextColor,mTextColor);
            mEnableLongLines = attr.getBoolean(R.styleable.CustomView_EnableLongLines,mEnableLongLines);
            mEnableLittleLines = attr.getBoolean(R.styleable.CustomView_EnableLittleLines,mEnableLittleLines);
            mEnableText = attr.getBoolean(R.styleable.CustomView_EnableText,mEnableText);
            mEnabled = attr.getBoolean(R.styleable.CustomView_enable,mEnabled);
            attr.recycle();
        }

        if(mEnabled){
            mInnerCirclePaint.setColor(mInnerCircleColor);
            mMiddleCirclePaint.setColor(mMiddleCircleColor);
            mExteriorCirclePaint.setColor(mExteriorCircleColor);
            mValueMiddleCirclePaint.setColor(mValueMiddleCircleColor);
            if(mEnableLittleLines){ mLittleLinesPaint.setColor(mLittleLinesColor);} else { mLittleLinesPaint.setColor(mMiddleCircleColor);}
            if(mEnableLongLines){mLongLinesPaint.setColor(mLongLinesColor);}else{mLongLinesPaint.setColor(mMiddleCircleColor);}
            if (mEnableText){mTextPaint.setColor(mTextColor);}else{mTextPaint.setColor(mInnerCircleColor);}
        }else{
            mInnerCirclePaint.setColor(mDisabledColor);
            mMiddleCirclePaint.setColor(mDisabledColor);
            mExteriorCirclePaint.setColor(mDisabledColor);
            mValueMiddleCirclePaint.setColor(mDisabledColor);
            mLittleLinesPaint.setColor(mDisabledColor);
            mLongLinesPaint.setColor(mDisabledColor);
            mTextPaint.setColor(mDisabledColor);
        }
        mLittleLinesPaint.setStrokeWidth(5);
        mLongLinesPaint.setStrokeWidth(7);
       // mValueMiddleCirclePaint.setStrokeWidth(mMiddleCircleDiameter/2-mInnerCircleDiameter/2);

        mMiddleCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mExteriorCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mValueMiddleCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mInnerCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mLongLinesPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mLittleLinesPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTextPaint.setStyle(Paint.Style.FILL);

        mCenterPoint = new Point(getWidth()+mExteriorCircleDiameter/2,mExteriorCircleDiameter/2+getHeight());
        mLongLinesPaint.setStrokeCap(Paint.Cap.ROUND);
        mLittleLinesPaint.setStrokeCap(Paint.Cap.ROUND);
        mExteriorCirclePaint.setStrokeCap(Paint.Cap.ROUND);
        mMiddleCirclePaint.setStrokeCap(Paint.Cap.ROUND);
        mInnerCirclePaint.setStrokeCap(Paint.Cap.ROUND);

    }
    /**
     * fonction qui converti des dp en nombre de pixel équivalent en fonction des dimensions de l'appareil
     * @param dp    une valeur en dp (density equivalent pixels). C'est ce qu'on doit convertir en pixel
     * @return      un nombre flottant représentant les pixels équivanlent au dp en fonction des dimensions de l'appareil
     */
    private float dpToPixel(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
    /**
     * ajuste les dimensions des différents éléments en fonction de l'espace disponible dans le canvas.
     */
    private void adaptDims() {
        float pt = getPaddingTop();
        float pb = getPaddingBottom();
        float pr = getPaddingRight();
        float pl = getPaddingLeft();

        final float minExteriorCircleDiameter = dpToPixel(MIN_MIDDLE_CIRCLE_DIAMETER);

        /*************************traitement de la largeur du curseur ************************/
        //cas du plus petit espace affecté (annulation des paddings et réduction du curseur)
        if (minExteriorCircleDiameter > getWidth()) {
            mExteriorCircleDiameter = getWidth();
            mMiddleCircleDiameter = getWidth() - 2;
            mInnerCircleDiameter = getWidth() - 107;
            pr = 0;
            pl = 0;

        } else if (minExteriorCircleDiameter + pl + pr > getWidth()) {
            mExteriorCircleDiameter = (int) dpToPixel(MIN_EXTERIOR_CIRCLE_DIAMETER);
            mMiddleCircleDiameter = (int) dpToPixel(MIN_MIDDLE_CIRCLE_DIAMETER);
            mInnerCircleDiameter = (int) dpToPixel(MIN_INNER_CIRCLE_DIAMETER);
            float ratio = (getWidth() - minExteriorCircleDiameter) / (pl + pr);
            pl *= ratio;
            pr *= ratio;
        } else if (mExteriorCircleDiameter + pl + pr > getWidth()) {
            float ratio = (getWidth() - minExteriorCircleDiameter) / (pl + pr);
            pl *= ratio;
            pr *= ratio;
            mMiddleCircleDiameter = getWidth() - 2 - (int)pl - (int)pr;
            mInnerCircleDiameter = getWidth() - 107- (int)pl - (int)pr;
            mExteriorCircleDiameter = (int) (getWidth() - pl - pr);
        }
        /*************************traitement de la longeur du customView ************************/

       if (minExteriorCircleDiameter > getHeight()) {
            mExteriorCircleDiameter = getHeight();
            mMiddleCircleDiameter = getHeight() - 2;
            mInnerCircleDiameter = getHeight() - 107;
            pt = 0;
            pb = 0;

        } else if (minExteriorCircleDiameter + pt + pb > getHeight()) {
            mExteriorCircleDiameter = (int) dpToPixel(MIN_EXTERIOR_CIRCLE_DIAMETER);
            mMiddleCircleDiameter = (int) dpToPixel(MIN_MIDDLE_CIRCLE_DIAMETER);
            mInnerCircleDiameter = (int) dpToPixel(MIN_INNER_CIRCLE_DIAMETER);
            float ratio = (getHeight() - minExteriorCircleDiameter) / (pb + pt);
            pb *= ratio;
            pt *= ratio;
        } else if (mExteriorCircleDiameter + pb + pt > getHeight()) {
            float ratio = (getHeight() - minExteriorCircleDiameter) / (pb + pt);
            pb *= ratio;
            pt *= ratio;
            mMiddleCircleDiameter = getHeight() - 2 - (int)pb - (int)pt;
            mInnerCircleDiameter = getHeight() - 107- (int)pb - (int)pt;
            mExteriorCircleDiameter = (int) (getHeight() - pb - pt);
        }

        setPadding((int)pl,(int)pt,(int)pr,(int)pb);
        mCenterPoint = new Point(getWidth()/2+(int)pr-(int)pl/2,getHeight()/2+(int)pt-(int)pb/2);

    }

    /**
     * Fonction qui dessine sur la fenetre les formes que l'on veut (petit, moyen, et grand cercle,
     * arc de cercle de la valeur, traits courts et longs des quarts et seizièmes et du texte.
     * Cette fonction est appelé très souvent pour mettre à jour la fenetre.
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        adaptDims();
        // on utilise des cercles et pas des Ovals pour ne pas avoir de déformation (pas un cercle) quand on tourne l'écran
        canvas.drawCircle(mCenterPoint.x,mCenterPoint.y,mExteriorCircleDiameter/2,mExteriorCirclePaint);
        canvas.drawCircle(mCenterPoint.x,mCenterPoint.y,mMiddleCircleDiameter/2,mMiddleCirclePaint);
        if(mValue<50){

            canvas.drawArc(mCenterPoint.x - mMiddleCircleDiameter/2,
                     mCenterPoint.y - mMiddleCircleDiameter/2,
                     mCenterPoint.x+mMiddleCircleDiameter/2,
                     mCenterPoint.y+mMiddleCircleDiameter/2,
                     -90,mValue*360/100,true,mValueMiddleCirclePaint);
        }
        if (mValue>50) {
            /* on a deux arcs parce que je n'arrivais pas à faire le tour complet avec un seul arc.
             il y avait un trait en plus vers 80 % du tour complet qui ne convenait pas au cahier des charges
             du coup pour les valeurs supérieures à 50% j'ai fait un premier demi-cercle qui partait du haut
             pour aller à 50 % puis un arc de cercle partant du bas pour completer l'arc en fonction de
             la valeur du curseur.
             */

            canvas.drawArc(mCenterPoint.x - mMiddleCircleDiameter/2,
                    mCenterPoint.y - mMiddleCircleDiameter/2,
                    mCenterPoint.x+mMiddleCircleDiameter/2,
                    mCenterPoint.y+mMiddleCircleDiameter/2,
                    -90,50*360/100,true,mValueMiddleCirclePaint);

            canvas.drawArc(mCenterPoint.x - mMiddleCircleDiameter/2,
                    mCenterPoint.y - mMiddleCircleDiameter/2,
                    mCenterPoint.x+mMiddleCircleDiameter/2,
                    mCenterPoint.y+mMiddleCircleDiameter/2,
                    90,(mValue-50)*360/100,true,mValueMiddleCirclePaint);

        }
        // il a fallut faire le cercle intérieur après car les dessins se supperposent.
        canvas.drawCircle(mCenterPoint.x,mCenterPoint.y,mInnerCircleDiameter/2,mInnerCirclePaint);//(float)toRadians(mValue*360/100)
        mTextPaint.setTextSize(50);
        canvas.drawText(String.valueOf((int)(mValue))+ '%',mCenterPoint.x-40,mCenterPoint.y-20,mTextPaint);

        //dessiner les lignes 1/16eme , les plus courtes

        for (int i = 0; i < 8; i++) {
            if (mEnableLittleLines) {
                canvas.drawLine((float) (mCenterPoint.x + sin(toRadians(22.5 + 45 * i)) * ((mMiddleCircleDiameter/2)*0.8)),
                        (float) (mCenterPoint.y + cos(toRadians(22.5 + 45 * i)) * ((mMiddleCircleDiameter/2 )*0.8)),
                        (float) (mCenterPoint.x + sin(toRadians(22.5 + 45 * i)) * ((mInnerCircleDiameter/2) * 1.3)),
                        (float) (mCenterPoint.y + cos(toRadians(22.5 + 45 * i)) * (( mInnerCircleDiameter/2) *1.3)),
                        mLittleLinesPaint);
            }
            if (mEnableLongLines) {
                canvas.drawLine((float) (mCenterPoint.x + sin(toRadians(45 * i)) * ((mMiddleCircleDiameter/2 ) * 0.9)),
                        (float) (mCenterPoint.y + cos(toRadians(45 * i)) * ((mMiddleCircleDiameter/2 ) * 0.9)),
                        (float) (mCenterPoint.x + sin(toRadians(45 * i)) * ((mInnerCircleDiameter/2) * 1.2)),
                        (float) (mCenterPoint.y + cos(toRadians(45 * i)) * ((mInnerCircleDiameter/2) * 1.2)),
                        mLongLinesPaint);
            }
        }

    }


    /**
     * détermine la position sur le canvas du centre du curseur
     *
     * @param value : valeur du customView
     * @return point de coordonnées du curseur dans le canvas
     */
    private Point toPos(float value) {
        int x, y;
        float longeur_arc,perimetre,angle;
        /*
        on transforme la valeur que l'on a en degré pour avoir un angle. pour ça on fait juste un produit en croix.
        Puis les coordonnées x et y osnt le sinus et le cosinus de cet angle par la distance de l'arc de cercle du milieu
         */
        angle=value*360/100; //en degré
        x= (int) (sin(Math.toRadians(90-angle))*((mMiddleCircleDiameter-mInnerCircleDiameter)+mInnerCircleDiameter));
        y=(int) (cos(Math.toRadians(90-angle))* ((mMiddleCircleDiameter-mInnerCircleDiameter)+mInnerCircleDiameter));
        return new Point(x, y);
    }


    /**
     * donne la valeur du curseur à partir du centre du point dans le canvas
     *
     * @param point point du curseur dans le canvas
     * @return valeur du curseur libre sur le curseur utilisé
     */
    private float toValue(Point point) {
        float ratio,alpha;
        // l'angle alpha est l'arctangeante de la distance entre le point et le centre suivant x sur celle suivant y.
        // Cette méthode ne marche pas quand on est à 25 % car il y a division par zéro
        if(mCenterPoint.y!=point.y) {
            alpha = (float) toDegrees(-Math.atan2((point.y - mCenterPoint.y),(point.x - mCenterPoint.x)));

        }else {
            alpha = (float)(0.25*360);
        }
        ratio = (90-alpha)/360;
        // on s'assure que le ratio ne soit jamais négatif (entre 75 et 100 % le ratio est négatif par nature)

        if (ratio <0 ){
             ratio = (float)((0.25+ratio)+0.75);
        }
        //Pour qu'on ne dépasse pas 100%, on met le ratio à 1 quand le point est sur la moitié droite du curseur et qu'avant on était au dessus de 90%.
        if (mValue>90){
            if (point.x >mCenterPoint.x){
                ratio = 1;
                return ratio*100;
            }}
        // Pour qu'on aille pas à 100% quand on vient de 0%, on met le ratio à 0 quand on est sur la moitié gauche du curseur et qu'avant on était en dessous de 10 %.
        if ( mValue<10){
                if (point.x <mCenterPoint.x){
                    ratio = 0;
                    return ratio*100;
                }
            }
        return ratio*100;
    }

    /**
     * interface à implémenter dans le main activity
     */
    public interface CustomViewChangeListener{
        void onChange(float value);
        void onDoubleClick(float value);
    }

    /**
     * fonction qui prend en charge les évènements qui se passent dans le canvas et renvoit à des fonctions
     * @param event : mouvement du doigt sur la fenetre, presse du doigt, click ...
     * @return vrai quand l'action est finie
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            // ici les évènements qui nous intéressent sont le mouvement du doigt sur la fenetre et le double click
            case MotionEvent.ACTION_MOVE:
                if (!mDisabledMove) {
                    mValue = toValue(new Point((int) event.getX(), (int) event.getY()));
                    if (mCustomViewChangeListener != null)
                        mCustomViewChangeListener.onChange(mValue);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_DOWN:
                if (event.getY() > (mCenterPoint.y - mInnerCircleDiameter / 2) & event.getY() < (mCenterPoint.y + mInnerCircleDiameter / 2) & event.getX() > (mCenterPoint.x - mInnerCircleDiameter / 2) & event.getX() < (mCenterPoint.x + mInnerCircleDiameter / 2)) {
                    if (mDoubleClick) {
                        mDisabledMove = true;
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mDisabledMove = false;
                            }
                        }, 200);
                        mValue = mMin;
                        if (mCustomViewChangeListener != null) {
                            mCustomViewChangeListener.onDoubleClick(mValue);

                        }
                        invalidate();
                    } else {
                        mDoubleClick = true;
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mDoubleClick = false;
                            }
                        }, 500);
                    }
                }
                    invalidate();
                    break;
                    default:
                }

        return true;
           }


/****************************************************************************/
/*************************Sauvegarde et restauration d'état*******************/
/*****************************************************************************/
    /**
     * classe pour garder la valeur du curseur quand on change de sens d'écran.
     */
    protected class SavedState extends BaseSavedState{
        private float mSavedValue;
        //utilisé par onSaveInstanceState
        public SavedState(Parcelable superState){
            super(superState);
            mSavedValue= mValue;
        }
        // constructeur utilisé par le créator pour recréer la classe SavedState
        private  SavedState(Parcel in){
            super(in);
            mSavedValue = in.readFloat();
        }
        public final Creator<SavedState> CREATOR = new Creator<SavedState>(){
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[0];
            }

        };

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloat(mSavedValue);
        }
    }

    /**
     * fonction qui récupère la valeur du curseur
     * @return
     */
    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        return savedState;
    }

    /**
     * fonction qui renvoit la valeur courante du curseur après un changement de type de fenetre
     * @param state
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(!(state instanceof SavedState)){
            super.onRestoreInstanceState(state);
            return;
        }else{
            super.onRestoreInstanceState(((SavedState)state).getSuperState());
            mValue = ((SavedState)state).mSavedValue;
        }

    }
}
